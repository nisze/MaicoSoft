package com.faculdae.maiconsoft_api.dto.auth;

import lombok.Builder;

@Builder
public record LoginResponseDTO(
        String token,
        String codigoAcesso,
        String nome,
        String role
) {
}
