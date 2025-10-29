package com.faculdae.maiconsoft_api.specification;

import com.faculdae.maiconsoft_api.entities.Cliente;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification para filtros avançados de Cliente
 * Permite construir consultas dinâmicas com múltiplos critérios
 */
public class ClienteSpecification {

    /**
     * Constrói Specification com base nos parâmetros de filtro
     * @param codigo Código do cliente (busca exata)
     * @param razaoSocial Razão social (busca por contenção)
     * @param nomeFantasia Nome fantasia (busca por contenção)
     * @param tipo Tipo do cliente (F=Física, J=Jurídica)
     * @param cpfCnpj CPF/CNPJ (busca exata)
     * @param cidade Cidade (busca por contenção)
     * @param estado Estado (busca exata)
     * @param email Email (busca por contenção)
     * @param dataInicialCadastro Data inicial do cadastro
     * @param dataFinalCadastro Data final do cadastro
     * @return Specification construída
     */
    public static Specification<Cliente> build(
            String codigo,
            String razaoSocial,
            String nomeFantasia,
            String tipo,
            String cpfCnpj,
            String cidade,
            String estado,
            String email,
            LocalDate dataInicialCadastro,
            LocalDate dataFinalCadastro
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por código (busca exata)
            if (StringUtils.hasText(codigo)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.upper(root.get("codigo")),
                        codigo.toUpperCase()
                ));
            }

            // Filtro por razão social (busca por contenção - case insensitive)
            if (StringUtils.hasText(razaoSocial)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("razaoSocial")),
                        "%" + razaoSocial.toUpperCase() + "%"
                ));
            }

            // Filtro por nome fantasia (busca por contenção - case insensitive)
            if (StringUtils.hasText(nomeFantasia)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("nomeFantasia")),
                        "%" + nomeFantasia.toUpperCase() + "%"
                ));
            }

            // Filtro por tipo (busca exata)
            if (StringUtils.hasText(tipo)) {
                predicates.add(criteriaBuilder.equal(
                        root.get("tipo"),
                        tipo.toUpperCase()
                ));
            }

            // Filtro por CPF/CNPJ (busca exata)
            if (StringUtils.hasText(cpfCnpj)) {
                predicates.add(criteriaBuilder.equal(
                        root.get("cpfCnpj"),
                        cpfCnpj
                ));
            }

            // Filtro por cidade (busca por contenção - case insensitive)
            if (StringUtils.hasText(cidade)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("cidade")),
                        "%" + cidade.toUpperCase() + "%"
                ));
            }

            // Filtro por estado (busca exata)
            if (StringUtils.hasText(estado)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.upper(root.get("estado")),
                        estado.toUpperCase()
                ));
            }

            // Filtro por email (busca por contenção - case insensitive)
            if (StringUtils.hasText(email)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("email")),
                        "%" + email.toUpperCase() + "%"
                ));
            }

            // Filtro por data inicial de cadastro
            if (dataInicialCadastro != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("datahoraCadastro"),
                        dataInicialCadastro.atStartOfDay()
                ));
            }

            // Filtro por data final de cadastro
            if (dataFinalCadastro != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("datahoraCadastro"),
                        dataFinalCadastro.atTime(23, 59, 59)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Specification para busca por texto livre
     * Busca em múltiplos campos: código, razão social, nome fantasia, CPF/CNPJ
     * @param searchText Texto para busca
     * @return Specification para busca livre
     */
    public static Specification<Cliente> searchByText(String searchText) {
        if (!StringUtils.hasText(searchText)) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            String searchPattern = "%" + searchText.toUpperCase() + "%";
            
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.upper(root.get("codigo")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.upper(root.get("razaoSocial")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.upper(root.get("nomeFantasia")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.upper(root.get("cpfCnpj")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.upper(root.get("email")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.upper(root.get("telefone")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.upper(root.get("contato")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.upper(root.get("cidade")), searchPattern)
            );
        };
    }

    /**
     * Specification para buscar clientes ativos (sem data de exclusão)
     * @return Specification para clientes ativos
     */
    public static Specification<Cliente> isActive() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNull(root.get("dataExclusao"));
    }

    /**
     * Specification para buscar clientes por usuário cadastrador
     * @param usuarioId ID do usuário cadastrador
     * @return Specification para filtro por usuário
     */
    public static Specification<Cliente> byUsuarioCadastro(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("usuarioCadastro").get("id"), usuarioId);
    }

    /**
     * Specification para buscar clientes por faixa de CEP
     * @param cepInicial CEP inicial
     * @param cepFinal CEP final
     * @return Specification para filtro por faixa de CEP
     */
    public static Specification<Cliente> byCepRange(String cepInicial, String cepFinal) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(cepInicial)) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("cep"), cepInicial
                ));
            }

            if (StringUtils.hasText(cepFinal)) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("cep"), cepFinal
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}