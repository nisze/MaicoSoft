package com.faculdae.maiconsoft_api.dto.venda;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para filtros avançados de consulta de vendas
 * Suporte para múltiplos critérios de busca e ordenação
 */
@Data
@Builder
public class VendaRequestFilterDTO {

    // ========== FILTROS DE CLIENTE ==========
    
    /**
     * Nome do cliente (busca parcial na razão social)
     */
    private String clienteNome;
    
    /**
     * Código do cliente (busca exata)
     */
    private String clienteCodigo;

    // ========== FILTROS DE VENDA ==========
    
    /**
     * Status da venda (ORCAMENTO, FINALIZADA, CANCELADA)
     */
    private String status;
    
    /**
     * Lista de status válidos para busca múltipla
     */
    private List<String> statusList;
    
    /**
     * Número do orçamento (busca parcial)
     */
    private String numeroOrcamento;

    // ========== FILTROS DE VALOR ==========
    
    /**
     * Valor total mínimo
     */
    private BigDecimal valorMinimo;
    
    /**
     * Valor total máximo
     */
    private BigDecimal valorMaximo;
    
    /**
     * Desconto mínimo aplicado
     */
    private BigDecimal descontoMinimo;

    // ========== FILTROS DE DATA ==========
    
    /**
     * Data de venda inicial (formato: YYYY-MM-DD)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVendaInicio;
    
    /**
     * Data de venda final (formato: YYYY-MM-DD)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVendaFim;
    
    /**
     * Data de cadastro inicial (formato: YYYY-MM-DD HH:mm:ss)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataCadastroInicio;
    
    /**
     * Data de cadastro final (formato: YYYY-MM-DD HH:mm:ss)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataCadastroFim;

    // ========== FILTROS DE CUPOM ==========
    
    /**
     * Código do cupom aplicado
     */
    private String cupomCodigo;
    
    /**
     * Filtrar apenas vendas com cupom aplicado
     */
    private Boolean comCupom;

    // ========== FILTROS DE USUÁRIO ==========
    
    /**
     * ID do usuário cadastrador
     */
    private Long usuarioId;
    
    /**
     * Código de acesso do usuário cadastrador
     */
    private String usuarioCodigo;

    // ========== CONFIGURAÇÕES DE PAGINAÇÃO ==========
    
    /**
     * Página (0-based)
     */
    @Builder.Default
    private Integer page = 0;
    
    /**
     * Tamanho da página
     */
    @Builder.Default
    private Integer size = 20;
    
    /**
     * Campo para ordenação
     */
    @Builder.Default
    private String sortBy = "datahoraCadastro";
    
    /**
     * Direção da ordenação (asc/desc)
     */
    @Builder.Default
    private String sortDir = "desc";

    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Verifica se há filtros de data de venda aplicados
     */
    public boolean hasDataVendaFilter() {
        return dataVendaInicio != null || dataVendaFim != null;
    }
    
    /**
     * Verifica se há filtros de valor aplicados
     */
    public boolean hasValorFilter() {
        return valorMinimo != null || valorMaximo != null;
    }
    
    /**
     * Verifica se há filtros de usuário aplicados
     */
    public boolean hasUsuarioFilter() {
        return usuarioId != null || (usuarioCodigo != null && !usuarioCodigo.trim().isEmpty());
    }
    
    /**
     * Verifica se há filtros de cliente aplicados
     */
    public boolean hasClienteFilter() {
        return (clienteNome != null && !clienteNome.trim().isEmpty()) || 
               (clienteCodigo != null && !clienteCodigo.trim().isEmpty());
    }
}