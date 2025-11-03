package com.faculdae.maiconsoft_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "*")
@Tag(name = "File Upload", description = "Endpoints para upload de arquivos")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    // Pasta onde as imagens serão salvas (relativa ao projeto)
    @Value("${app.upload.directory:./uploads}")
    private String uploadDirectory;

    // Tamanho máximo do arquivo (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // Tipos de arquivo permitidos
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    @PostMapping("/profile-photo")
    @Operation(summary = "Upload de foto de perfil", description = "Faz upload da foto de perfil do usuário")
    @ApiResponse(responseCode = "200", description = "Upload realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Arquivo inválido")
    @ApiResponse(responseCode = "413", description = "Arquivo muito grande")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @Parameter(description = "Arquivo de imagem") @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID do usuário") @RequestParam("userId") Long userId) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validações
            if (file.isEmpty()) {
                response.put("error", "Arquivo não pode estar vazio");
                return ResponseEntity.badRequest().body(response);
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                response.put("error", "Arquivo muito grande. Máximo permitido: 5MB");
                return ResponseEntity.status(413).body(response);
            }

            String contentType = file.getContentType();
            boolean validType = false;
            for (String allowedType : ALLOWED_CONTENT_TYPES) {
                if (allowedType.equals(contentType)) {
                    validType = true;
                    break;
                }
            }

            if (!validType) {
                response.put("error", "Tipo de arquivo não permitido. Use apenas: JPEG, PNG, GIF ou WebP");
                return ResponseEntity.badRequest().body(response);
            }

            // Criar diretório se não existir
            Path uploadPath = Paths.get(uploadDirectory + "/profiles");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gerar nome único para o arquivo
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "user_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;

            // Caminho completo do arquivo
            Path filePath = uploadPath.resolve(fileName);

            // Salvar arquivo
            Files.copy(file.getInputStream(), filePath);

            // Retornar caminho relativo
            String relativePath = "uploads/profiles/" + fileName;
            
            response.put("message", "Upload realizado com sucesso");
            response.put("filePath", relativePath);
            response.put("fileName", fileName);

            logger.info("Upload de foto de perfil realizado: {} para usuário {}", relativePath, userId);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Erro ao fazer upload da foto de perfil", e);
            response.put("error", "Erro interno do servidor ao processar upload");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/profile-photo")
    @Operation(summary = "Deletar foto de perfil", description = "Remove a foto de perfil do usuário")
    public ResponseEntity<Map<String, String>> deleteProfilePhoto(
            @Parameter(description = "Caminho da foto") @RequestParam("filePath") String filePath) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Remover "uploads/" do início se presente
            String cleanPath = filePath.startsWith("uploads/") ? filePath.substring(8) : filePath;
            Path fileToDelete = Paths.get(uploadDirectory, cleanPath);

            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
                response.put("message", "Foto removida com sucesso");
                logger.info("Foto de perfil removida: {}", filePath);
            } else {
                response.put("message", "Arquivo não encontrado");
            }

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Erro ao deletar foto de perfil", e);
            response.put("error", "Erro ao deletar arquivo");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/comprovante-venda")
    @Operation(summary = "Upload de comprovante de venda", description = "Faz upload do comprovante de venda")
    @ApiResponse(responseCode = "200", description = "Upload realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Arquivo inválido")
    @ApiResponse(responseCode = "413", description = "Arquivo muito grande")
    public ResponseEntity<Map<String, String>> uploadComprovanteVenda(
            @Parameter(description = "Arquivo de imagem/documento") @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID da venda") @RequestParam("vendaId") Long vendaId) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validações
            if (file.isEmpty()) {
                response.put("error", "Arquivo não pode estar vazio");
                return ResponseEntity.badRequest().body(response);
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                response.put("error", "Arquivo muito grande. Tamanho máximo: 5MB");
                return ResponseEntity.status(413).body(response);
            }

            // Permitir também PDFs para comprovantes
            String contentType = file.getContentType();
            boolean isValidType = false;
            for (String allowedType : ALLOWED_CONTENT_TYPES) {
                if (allowedType.equals(contentType)) {
                    isValidType = true;
                    break;
                }
            }
            
            // Adicionar suporte a PDF
            if ("application/pdf".equals(contentType)) {
                isValidType = true;
            }
            
            if (!isValidType) {
                response.put("error", "Tipo de arquivo não suportado. Use JPG, PNG, GIF, WEBP ou PDF");
                return ResponseEntity.badRequest().body(response);
            }

            // Criar diretório se não existir
            Path uploadPath = Paths.get(uploadDirectory, "comprovantes");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Diretório de comprovantes criado: {}", uploadPath);
            }

            // Gerar nome único para o arquivo
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String filename = "comprovante_venda_" + vendaId + "_" + UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(filename);

            // Salvar arquivo
            Files.copy(file.getInputStream(), filePath);
            logger.info("Comprovante de venda salvo: {}", filePath);

            // Retornar caminho relativo
            String relativePath = "comprovantes/" + filename;
            
            response.put("message", "Comprovante de venda enviado com sucesso");
            response.put("filename", filename);
            response.put("path", relativePath);
            response.put("vendaId", vendaId.toString());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Erro ao fazer upload do comprovante de venda", e);
            response.put("error", "Erro interno do servidor");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/comprovante-venda/{vendaId}")
    @Operation(summary = "Deletar comprovante de venda", description = "Remove o comprovante de venda do servidor")
    public ResponseEntity<Map<String, String>> deleteComprovanteVenda(
            @Parameter(description = "ID da venda") @PathVariable Long vendaId,
            @Parameter(description = "Nome do arquivo") @RequestParam String filename) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            Path filePath = Paths.get(uploadDirectory, "comprovantes", filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Comprovante de venda deletado: {}", filePath);
                response.put("message", "Comprovante de venda deletado com sucesso");
            } else {
                response.put("message", "Arquivo não encontrado");
            }

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Erro ao deletar comprovante de venda", e);
            response.put("error", "Erro ao deletar arquivo");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/comprovante/{filename}")
    @Operation(summary = "Visualizar comprovante", description = "Retorna o arquivo do comprovante para visualização")
    public ResponseEntity<byte[]> getComprovante(
            @Parameter(description = "Nome do arquivo") @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadDirectory, "comprovantes", filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            
            // Determinar tipo de conteúdo baseado na extensão
            String contentType = "application/octet-stream";
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            
            switch (extension) {
                case "jpg", "jpeg" -> contentType = "image/jpeg";
                case "png" -> contentType = "image/png";
                case "gif" -> contentType = "image/gif";
                case "webp" -> contentType = "image/webp";
                case "pdf" -> contentType = "application/pdf";
                case "doc" -> contentType = "application/msword";
                case "docx" -> contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(fileContent);

        } catch (IOException e) {
            logger.error("Erro ao buscar comprovante: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}