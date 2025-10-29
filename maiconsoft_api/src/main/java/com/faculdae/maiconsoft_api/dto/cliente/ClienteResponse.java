package com.faculdae.maiconsoft_api.dto.cliente;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO para resposta paginada de clientes
 * Usado em listagens com paginação
 */
@Builder
@Getter
@Setter
public class ClienteResponse {
    
    private List<ClienteResponseDTO> clientes;
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;
}