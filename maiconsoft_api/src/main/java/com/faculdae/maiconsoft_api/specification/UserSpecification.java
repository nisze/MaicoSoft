package com.faculdae.maiconsoft_api.specification;

import com.faculdae.maiconsoft_api.dto.user.UserRequestFilterDTO;
import com.faculdae.maiconsoft_api.entities.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> build(UserRequestFilterDTO filter) {
        List<Specification<User>> specs = new ArrayList<>();

        if (StringUtils.hasText(filter.nome())) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), "%" + filter.nome().toLowerCase() + "%"));
        }

        if (StringUtils.hasText(filter.email())) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + filter.email().toLowerCase() + "%"));
        }

        if (StringUtils.hasText(filter.cpf())) {
            specs.add((root, query, cb) -> cb.equal(root.get("cpf"), filter.cpf()));
        }

        if (StringUtils.hasText(filter.roleName())) {
            specs.add((root, query, cb) ->
                    cb.equal(root.join("userRole").get("roleName"), filter.roleName()));
        }

        if (StringUtils.hasText(filter.telefone())) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("telefone")), "%" + filter.telefone().toLowerCase() + "%"));
        }
        
        if (StringUtils.hasText(filter.codigoAcesso())) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("codigoAcesso")), "%" + filter.codigoAcesso().toLowerCase() + "%"));
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }
}
