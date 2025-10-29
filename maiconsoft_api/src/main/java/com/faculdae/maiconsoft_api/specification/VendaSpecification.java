package com.faculdae.maiconsoft_api.specification;

import com.faculdae.maiconsoft_api.entities.Cliente;
import com.faculdae.maiconsoft_api.entities.Cupom;
import com.faculdae.maiconsoft_api.entities.User;
import com.faculdae.maiconsoft_api.entities.Venda;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Specifications para consultas dinâmicas da entidade Venda
 */
public class VendaSpecification {

    /**
     * Filtra vendas por nome do cliente (razão social)
     * @param clienteNome Nome do cliente
     * @return Specification
     */
    public static Specification<Venda> hasClienteNome(String clienteNome) {
        return (root, query, criteriaBuilder) -> {
            if (clienteNome == null || clienteNome.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Venda, Cliente> clienteJoin = root.join("cliente", JoinType.INNER);
            return criteriaBuilder.like(
                criteriaBuilder.lower(clienteJoin.get("razaoSocial")),
                "%" + clienteNome.toLowerCase() + "%"
            );
        };
    }

    /**
     * Filtra vendas por status
     * @param status Status da venda
     * @return Specification
     */
    public static Specification<Venda> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Filtra vendas com data de venda após uma data específica
     * @param dataInicio Data inicial
     * @return Specification
     */
    public static Specification<Venda> hasDataVendaAfter(LocalDate dataInicio) {
        return (root, query, criteriaBuilder) -> {
            if (dataInicio == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("dataVenda"), dataInicio);
        };
    }

    /**
     * Filtra vendas com data de venda antes de uma data específica
     * @param dataFim Data final
     * @return Specification
     */
    public static Specification<Venda> hasDataVendaBefore(LocalDate dataFim) {
        return (root, query, criteriaBuilder) -> {
            if (dataFim == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("dataVenda"), dataFim);
        };
    }

    /**
     * Filtra vendas por período (entre duas datas)
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Specification
     */
    public static Specification<Venda> hasDataVendaBetween(LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, criteriaBuilder) -> {
            if (dataInicio == null && dataFim == null) {
                return criteriaBuilder.conjunction();
            }
            if (dataInicio == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("dataVenda"), dataFim);
            }
            if (dataFim == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dataVenda"), dataInicio);
            }
            return criteriaBuilder.between(root.get("dataVenda"), dataInicio, dataFim);
        };
    }

    /**
     * Filtra vendas por número do orçamento (LIKE)
     * @param numeroOrcamento Número do orçamento
     * @return Specification
     */
    public static Specification<Venda> hasNumeroOrcamento(String numeroOrcamento) {
        return (root, query, criteriaBuilder) -> {
            if (numeroOrcamento == null || numeroOrcamento.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("numeroOrcamento"), "%" + numeroOrcamento + "%");
        };
    }

    /**
     * Filtra vendas por valor total mínimo
     * @param valorMinimo Valor mínimo
     * @return Specification
     */
    public static Specification<Venda> hasValorTotalGreaterThan(BigDecimal valorMinimo) {
        return (root, query, criteriaBuilder) -> {
            if (valorMinimo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("valorTotal"), valorMinimo);
        };
    }

    /**
     * Filtra vendas por valor total máximo
     * @param valorMaximo Valor máximo
     * @return Specification
     */
    public static Specification<Venda> hasValorTotalLessThan(BigDecimal valorMaximo) {
        return (root, query, criteriaBuilder) -> {
            if (valorMaximo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("valorTotal"), valorMaximo);
        };
    }

    /**
     * Filtra vendas por range de valor total
     * @param valorMinimo Valor mínimo
     * @param valorMaximo Valor máximo
     * @return Specification
     */
    public static Specification<Venda> hasValorTotalBetween(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return (root, query, criteriaBuilder) -> {
            if (valorMinimo == null && valorMaximo == null) {
                return criteriaBuilder.conjunction();
            }
            if (valorMinimo == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("valorTotal"), valorMaximo);
            }
            if (valorMaximo == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("valorTotal"), valorMinimo);
            }
            return criteriaBuilder.between(root.get("valorTotal"), valorMinimo, valorMaximo);
        };
    }

    /**
     * Filtra vendas por usuário cadastrador
     * @param usuarioId ID do usuário cadastrador
     * @return Specification
     */
    public static Specification<Venda> hasUsuarioCadastro(Long usuarioId) {
        return (root, query, criteriaBuilder) -> {
            if (usuarioId == null) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Venda, User> usuarioJoin = root.join("usuarioCadastro", JoinType.INNER);
            return criteriaBuilder.equal(usuarioJoin.get("id"), usuarioId);
        };
    }

    /**
     * Filtra vendas por código do usuário cadastrador
     * @param codigoUsuario Código de acesso do usuário
     * @return Specification
     */
    public static Specification<Venda> hasUsuarioCadastroCodigo(String codigoUsuario) {
        return (root, query, criteriaBuilder) -> {
            if (codigoUsuario == null || codigoUsuario.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Venda, User> usuarioJoin = root.join("usuarioCadastro", JoinType.INNER);
            return criteriaBuilder.equal(usuarioJoin.get("codigoAcesso"), codigoUsuario);
        };
    }

    /**
     * Filtra vendas por período de cadastro
     * @param dataInicio Data inicial de cadastro
     * @param dataFim Data final de cadastro
     * @return Specification
     */
    public static Specification<Venda> hasDataCadastroBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return (root, query, criteriaBuilder) -> {
            if (dataInicio == null && dataFim == null) {
                return criteriaBuilder.conjunction();
            }
            if (dataInicio == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("datahoraCadastro"), dataFim);
            }
            if (dataFim == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("datahoraCadastro"), dataInicio);
            }
            return criteriaBuilder.between(root.get("datahoraCadastro"), dataInicio, dataFim);
        };
    }

    /**
     * Filtra vendas por código do cliente
     * @param codigoCliente Código do cliente
     * @return Specification
     */
    public static Specification<Venda> hasClienteCodigo(String codigoCliente) {
        return (root, query, criteriaBuilder) -> {
            if (codigoCliente == null || codigoCliente.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Venda, Cliente> clienteJoin = root.join("cliente", JoinType.INNER);
            return criteriaBuilder.equal(clienteJoin.get("codigo"), codigoCliente);
        };
    }

    /**
     * Filtra vendas que possuem cupom aplicado
     * @return Specification
     */
    public static Specification<Venda> hasCupom() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNotNull(root.get("cupom"));
    }

    /**
     * Filtra vendas por código do cupom
     * @param codigoCupom Código do cupom
     * @return Specification
     */
    public static Specification<Venda> hasCupomCodigo(String codigoCupom) {
        return (root, query, criteriaBuilder) -> {
            if (codigoCupom == null || codigoCupom.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Venda, Cupom> cupomJoin = root.join("cupom", JoinType.INNER);
            return criteriaBuilder.equal(cupomJoin.get("codigo"), codigoCupom);
        };
    }

    /**
     * Filtra vendas sem cupom aplicado
     * @return Specification
     */
    public static Specification<Venda> hasNoCupom() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNull(root.get("cupom"));
    }

    /**
     * Filtra vendas com desconto maior que um valor específico
     * @param descontoMinimo Desconto mínimo
     * @return Specification
     */
    public static Specification<Venda> hasDescontoGreaterThan(BigDecimal descontoMinimo) {
        return (root, query, criteriaBuilder) -> {
            if (descontoMinimo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("valorDesconto"), descontoMinimo);
        };
    }

    /**
     * Filtra vendas por múltiplos status
     * @param statusList Lista de status válidos
     * @return Specification
     */
    public static Specification<Venda> hasStatusIn(java.util.List<String> statusList) {
        return (root, query, criteriaBuilder) -> {
            if (statusList == null || statusList.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statusList);
        };
    }
}