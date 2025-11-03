package com.faculdae.maiconsoft_api.dto.venda;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para requisições de criação/atualização de venda
 * Contém validações Bean Validation
 */
@Builder
public record VendaRequestDTO(
        
        @NotNull(message = "ID do cliente é obrigatório")
        Long clienteId,
        
        String cupomCodigo, // Código do cupom (opcional)
        
        @NotBlank(message = "Status é obrigatório")
        @Pattern(regexp = "PENDENTE|CONFIRMADA|CANCELADA|FINALIZADA", 
                message = "Status deve ser PENDENTE, CONFIRMADA, CANCELADA ou FINALIZADA")
        String status,
        
        @NotNull(message = "Valor bruto é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor bruto deve ser maior que zero")
        BigDecimal valorBruto,
        
        LocalDate dataVenda,
        
        @Size(max = 1000, message = "Observação deve ter no máximo 1000 caracteres")
        String observacao,
        
        // Caminho do arquivo de comprovante (opcional)
        @Size(max = 500, message = "Caminho do comprovante deve ter no máximo 500 caracteres")
        String comprovantePath
) {
}