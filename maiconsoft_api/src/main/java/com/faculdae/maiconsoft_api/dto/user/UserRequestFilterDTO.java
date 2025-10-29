package com.faculdae.maiconsoft_api.dto.user;

public record UserRequestFilterDTO (
        String codigoAcesso,
        String nome,
        String email,
        String cpf,
        String telefone,
        String roleName
){
}
