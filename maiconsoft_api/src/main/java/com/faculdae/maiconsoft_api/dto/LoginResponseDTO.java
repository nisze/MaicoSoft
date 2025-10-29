package com.faculdae.maiconsoft_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para resposta de login")
public class LoginResponseDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String nome;

    @Schema(description = "Email do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Código de acesso", example = "ADM001")
    private String codigoAcesso;

    @Schema(description = "Status ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Mensagem de sucesso", example = "Login realizado com sucesso")
    private String mensagem;

    public LoginResponseDTO() {}

    public LoginResponseDTO(Long id, String nome, String email, String codigoAcesso, Boolean ativo, String mensagem) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.codigoAcesso = codigoAcesso;
        this.ativo = ativo;
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}