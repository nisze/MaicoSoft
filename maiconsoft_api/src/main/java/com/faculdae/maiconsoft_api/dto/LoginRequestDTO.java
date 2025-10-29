package com.faculdae.maiconsoft_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO para requisições de login")
public class LoginRequestDTO {

    @NotBlank(message = "Código de acesso é obrigatório")
    @Schema(description = "Código de acesso do funcionário", example = "ADM001")
    private String codigoAcesso;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "123456")
    private String senha;

    // Getters e Setters
    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}