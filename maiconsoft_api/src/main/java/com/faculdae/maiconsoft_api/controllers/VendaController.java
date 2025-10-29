package com.faculdae.maiconsoft_api.controllers;

import com.faculdae.maiconsoft_api.dto.venda.VendaRequestDTO;
import com.faculdae.maiconsoft_api.dto.venda.VendaRequestFilterDTO;
import com.faculdae.maiconsoft_api.dto.venda.VendaResponse;
import com.faculdae.maiconsoft_api.dto.venda.VendaResponseDTO;
import com.faculdae.maiconsoft_api.services.venda.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller REST para operações de Venda
 * Endpoints protegidos por JWT com autorização baseada em roles
 */
@RestController
@RequestMapping("/api/vendas")
@Tag(name = "Vendas", description = "Endpoints para gestão de vendas e orçamentos")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class VendaController {

    private final VendaService vendaService;

    @Operation(summary = "Criar nova venda", 
               description = "Cria uma nova venda/orçamento com integração de email automática")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venda criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cliente ou cupom não encontrado")
    })
    @PostMapping
    
    public ResponseEntity<VendaResponseDTO> create(
            @Valid @RequestBody VendaRequestDTO vendaRequest) {
        
        log.info("Criando nova venda para cliente ID: {}", vendaRequest.clienteId());
        
        VendaResponseDTO response = vendaService.save(vendaRequest);
        
        log.info("Venda criada com sucesso - ID: {}, Orçamento: {}", 
                response.getIdVenda(), response.getNumeroOrcamento());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar venda por ID", 
               description = "Retorna dados completos da venda")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @GetMapping("/{id}")
    
    public ResponseEntity<VendaResponseDTO> findById(
            @Parameter(description = "ID da venda") @PathVariable Long id) {
        
        VendaResponseDTO response = vendaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar venda por número do orçamento", 
               description = "Retorna dados completos da venda pelo número do orçamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @GetMapping("/orcamento/{numeroOrcamento}")
    
    public ResponseEntity<VendaResponseDTO> findByNumeroOrcamento(
            @Parameter(description = "Número do orçamento") @PathVariable String numeroOrcamento) {
        
        VendaResponseDTO response = vendaService.findByNumeroOrcamento(numeroOrcamento);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar vendas com filtros", 
               description = "Lista vendas com filtros opcionais e paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping
    
    public ResponseEntity<VendaResponse> findAll(
            @Parameter(description = "Nome do cliente (busca parcial)") 
            @RequestParam(required = false) String clienteNome,
            
            @Parameter(description = "Status da venda (ORCAMENTO, FINALIZADA, CANCELADA)") 
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Data inicial (YYYY-MM-DD)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            
            @Parameter(description = "Data final (YYYY-MM-DD)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            
            @Parameter(description = "Página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "datahoraCadastro") String sortBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Listando vendas - Cliente: {}, Status: {}, Período: {} a {}, Página: {}", 
                clienteNome, status, dataInicio, dataFim, page);
        
        VendaResponse response = vendaService.findByFilter(
                clienteNome, status, dataInicio, dataFim, 
                page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar vendas por período", 
               description = "Busca vendas em um período específico com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Período inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping("/periodo")
    
    public ResponseEntity<VendaResponse> findByPeriodo(
            @Parameter(description = "Data inicial (obrigatória)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            
            @Parameter(description = "Data final (obrigatória)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            
            @Parameter(description = "Página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "dataVenda") String sortBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        // Validar período
        if (dataInicio.isAfter(dataFim)) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Buscando vendas por período: {} a {}", dataInicio, dataFim);
        
        VendaResponse response = vendaService.findByFilter(
                null, null, dataInicio, dataFim, 
                page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar vendas por status", 
               description = "Busca vendas por status específico com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping("/status/{status}")
    
    public ResponseEntity<VendaResponse> findByStatus(
            @Parameter(description = "Status da venda (ORCAMENTO, FINALIZADA, CANCELADA)") 
            @PathVariable String status,
            
            @Parameter(description = "Página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "datahoraCadastro") String sortBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Buscando vendas por status: {}", status);
        
        VendaResponse response = vendaService.findByFilter(
                null, status, null, null, 
                page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar vendas com filtros avançados", 
               description = "Busca vendas usando filtros avançados via POST body para flexibilidade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PostMapping("/filter")
    
    public ResponseEntity<VendaResponse> findByAdvancedFilter(
            @Valid @RequestBody VendaRequestFilterDTO filter) {
        
        log.info("Aplicando filtros avançados - Cliente: {}, Valor: {}-{}, Status: {}", 
                filter.getClienteNome(), filter.getValorMinimo(), 
                filter.getValorMaximo(), filter.getStatus());
        
        VendaResponse response = vendaService.findByAdvancedFilter(filter);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Relatório de vendas por usuário", 
               description = "Relatório de vendas agrupadas por usuário cadastrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping("/relatorio/usuario/{usuarioId}")
    
    public ResponseEntity<VendaResponse> findByUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            
            @Parameter(description = "Data inicial (YYYY-MM-DD)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            
            @Parameter(description = "Data final (YYYY-MM-DD)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            
            @Parameter(description = "Página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "50") int size) {
        
        log.info("Gerando relatório de vendas por usuário: {} - Período: {} a {}", 
                usuarioId, dataInicio, dataFim);
        
        VendaRequestFilterDTO filter = VendaRequestFilterDTO.builder()
                .usuarioId(usuarioId)
                .dataVendaInicio(dataInicio)
                .dataVendaFim(dataFim)
                .page(page)
                .size(size)
                .sortBy("dataVenda")
                .sortDir("desc")
                .build();
                
        VendaResponse response = vendaService.findByAdvancedFilter(filter);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar venda", 
               description = "Atualiza uma venda existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> update(
            @Parameter(description = "ID da venda") @PathVariable Long id,
            @Valid @RequestBody VendaRequestDTO vendaRequest) {
        
        log.info("Atualizando venda ID: {} para cliente ID: {}", id, vendaRequest.clienteId());
        
        VendaResponseDTO response = vendaService.update(id, vendaRequest);
        
        log.info("Venda atualizada com sucesso - ID: {}, Orçamento: {}", 
                response.getIdVenda(), response.getNumeroOrcamento());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Excluir venda", 
               description = "Exclui uma venda permanentemente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venda excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID da venda") @PathVariable Long id) {
        
        log.info("Excluindo venda ID: {}", id);
        
        vendaService.delete(id);
        
        log.info("Venda excluída com sucesso - ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }
}
