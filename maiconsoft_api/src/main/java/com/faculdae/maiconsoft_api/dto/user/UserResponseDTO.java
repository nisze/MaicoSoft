package com.faculdae.maiconsoft_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long idUser;
    private String codigoAcesso;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String roleName;
    private Boolean ativo;
    private String profilePhotoPath;
}
