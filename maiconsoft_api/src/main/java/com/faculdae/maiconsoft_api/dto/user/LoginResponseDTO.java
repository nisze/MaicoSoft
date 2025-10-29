package com.faculdae.maiconsoft_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta do login")
public record LoginResponseDTO(
    
    @Schema(description = "ID do usuário", example = "1")
    Long idUser,
    
    @Schema(description = "Nome do funcionário", example = "João Silva")
    String nome,
    
    @Schema(description = "Email do funcionário", example = "joao.silva@maiconsoft.com")
    String email,
    
    @Schema(description = "Código de acesso", example = "USR001")
    String codigoAcesso,
    
    @Schema(description = "Tipo de usuário", example = "FUNCIONARIO")
    String tipoUsuario,
    
    @Schema(description = "Status de sucesso do login", example = "true")
    Boolean success,
    
    @Schema(description = "Mensagem de sucesso", example = "Login realizado com sucesso")
    String message
    
) {}