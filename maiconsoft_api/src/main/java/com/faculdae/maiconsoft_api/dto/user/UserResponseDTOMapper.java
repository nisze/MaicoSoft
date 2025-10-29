package com.faculdae.maiconsoft_api.dto.user;

import com.faculdae.maiconsoft_api.entities.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserResponseDTOMapper implements Function<User, UserResponseDTO> {
    @Override
    public UserResponseDTO apply(User user) {
        return new UserResponseDTO(
                user.getIdUser(),
                user.getCodigoAcesso() != null ? user.getCodigoAcesso() : "USR" + String.format("%03d", user.getIdUser()),
                user.getNome(),
                user.getEmail(),
                user.getCpf(),
                user.getTelefone(),
                user.getTipoUsuario() != null ? user.getTipoUsuario() : "FUNCIONARIO"
        );
    }
}
