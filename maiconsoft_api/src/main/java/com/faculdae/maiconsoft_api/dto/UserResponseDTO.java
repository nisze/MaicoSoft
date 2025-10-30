package com.faculdae.maiconsoft_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO de resposta para usuário criado/atualizado")
public class UserResponseDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nome;

    @Schema(description = "Email do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Código de acesso gerado automaticamente", example = "ABC123")
    private String codigoAcesso;

    @Schema(description = "Status ativo do usuário", example = "true")
    private Boolean ativo;

    @Schema(description = "Nome da role/perfil do usuário", example = "FUNCIONARIO")
    private String roleName;

    @Schema(description = "Data de criação", example = "2025-10-23T15:12:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2025-10-23T15:12:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Mensagem informativa", example = "Usuário criado com sucesso! Use o código ABC123 para fazer login.")
    private String mensagem;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String nome, String email, String codigoAcesso, Boolean ativo, 
                          String roleName, LocalDateTime createdAt, LocalDateTime updatedAt, String mensagem) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.codigoAcesso = codigoAcesso;
        this.ativo = ativo;
        this.roleName = roleName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}