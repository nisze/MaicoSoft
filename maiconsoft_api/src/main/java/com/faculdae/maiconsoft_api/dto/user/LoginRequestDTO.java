package com.faculdae.maiconsoft_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para login de funcionário")
public record LoginRequestDTO(
    
    @Schema(description = "Código de acesso do funcionário", example = "ADM001")
    @NotBlank(message = "Código de acesso é obrigatório")
    @Size(min = 6, max = 6, message = "Código de acesso deve ter exatamente 6 caracteres")
    String codigoAcesso,
    
    @Schema(description = "Senha do funcionário", example = "123456")
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 50, message = "Senha deve ter entre 6 e 50 caracteres")
    String senha
    
) {}