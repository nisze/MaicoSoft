package com.faculdae.maiconsoft_api.controllers;

import com.faculdae.maiconsoft_api.services.user.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para gerenciar reset de senha
 */
@Slf4j
@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * Solicita reset de senha - envia token por email
     * POST /api/password-reset/request
     * Body: { "codigoAcesso": "ADM001" }
     */
    @PostMapping("/request")
    public ResponseEntity<?> solicitarReset(@RequestBody Map<String, String> request) {
        log.info("📥 Requisição de reset recebida: {}", request);
        
        String codigoAcesso = request.get("codigoAcesso");
        
        if (codigoAcesso == null || codigoAcesso.isBlank()) {
            log.warn("❌ Código de acesso vazio na requisição");
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Código de acesso é obrigatório"
            ));
        }

        log.info("🔍 Processando reset para código: {}", codigoAcesso);
        boolean sucesso = passwordResetService.solicitarResetSenha(codigoAcesso);
        
        if (sucesso) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Token enviado para o email cadastrado"
            ));
        } else {
            // Por segurança, não informamos se o código existe ou não
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Se o código existir e tiver email cadastrado, você receberá um token"
            ));
        }
    }

    /**
     * Valida o token
     * GET /api/password-reset/validate/{token}
     */
    @GetMapping("/validate/{token}")
    public ResponseEntity<?> validarToken(@PathVariable String token) {
        boolean valido = passwordResetService.validarToken(token).isPresent();
        
        return ResponseEntity.ok(Map.of(
            "valid", valido,
            "message", valido ? "Token válido" : "Token inválido ou expirado"
        ));
    }

    /**
     * Reseta a senha usando o token
     * POST /api/password-reset/reset
     * Body: { "token": "ABC123", "novaSenha": "novaSenha123" }
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetarSenha(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String novaSenha = request.get("novaSenha");
        
        if (token == null || token.isBlank() || novaSenha == null || novaSenha.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Token e nova senha são obrigatórios"
            ));
        }

        if (novaSenha.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "A senha deve ter no mínimo 6 caracteres"
            ));
        }

        boolean sucesso = passwordResetService.resetarSenha(token, novaSenha);
        
        if (sucesso) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Senha alterada com sucesso"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Token inválido ou expirado"
            ));
        }
    }

    /**
     * Endpoint de diagnóstico (apenas para dev)
     * GET /api/password-reset/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of(
            "tokensAtivos", passwordResetService.getTokensAtivos(),
            "message", "Serviço de reset de senha ativo"
        ));
    }
}