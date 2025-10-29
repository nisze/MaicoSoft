package com.faculdae.maiconsoft_api.dto.venda;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO para resposta paginada de vendas
 * Usado em listagens com paginação
 */
@Builder
@Getter
@Setter
public class VendaResponse {
    
    private List<VendaResponseDTO> vendas;
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;
}