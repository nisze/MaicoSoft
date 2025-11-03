package com.faculdae.maiconsoft_api.dto.venda;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de dados da venda
 * Usado em listagens e consultas individuais
 */
@Builder
@Getter
@Setter
public class VendaResponseDTO {
    
    private Long idVenda;
    private String numeroOrcamento;
    private String status;
    private BigDecimal valorBruto;
    private BigDecimal valorDesconto;
    private BigDecimal valorTotal;
    private LocalDate dataVenda;
    private LocalDateTime datahoraCadastro;
    private String observacao;
    
    // Dados do comprovante
    private String comprovantePath;
    private LocalDateTime comprovanteUploadDate;
    private Boolean comprovanteAnexado;
    
    // Dados do cliente
    private Long clienteId;
    private String clienteCodigo;
    private String clienteNome;
    
    // Dados do cupom (se aplicado)
    private Long cupomId;
    private String cupomCodigo;
    private String cupomNome;
    private Double cupomDesconto;
    
    // Dados do usuário cadastrador
    private String usuarioCadastroNome;
    private String usuarioCadastroCodigo;
}