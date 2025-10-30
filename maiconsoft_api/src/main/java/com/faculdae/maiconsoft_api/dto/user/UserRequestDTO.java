package com.faculdae.maiconsoft_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para requisições de criação/atualização de usuário")
public class UserRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Schema(description = "Email do usuário", example = "joao@email.com")
    private String email;

    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 dígitos")
    @Schema(description = "CPF do usuário (somente números)", example = "12345678901")
    private String cpf;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Schema(description = "Telefone do usuário", example = "11999999999")
    private String telefone;

    @Size(min = 4, max = 6, message = "Código de acesso deve ter entre 4 e 6 caracteres")
    @Schema(description = "Código de acesso (opcional - será gerado automaticamente se não fornecido)", example = "ADM001")
    private String codigoAcesso;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Schema(description = "Senha do usuário", example = "123456")
    private String senha;

    @Schema(description = "Status ativo do usuário", example = "true")
    private Boolean ativo = true;

    @Schema(description = "Nome da role/perfil do usuário", example = "FUNCIONARIO")
    private String roleName = "FUNCIONARIO";

    @Schema(description = "Caminho da foto de perfil do usuário", example = "uploads/profiles/user123.jpg")
    private String profilePhotoPath;

    // Getters e Setters
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

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

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }
}