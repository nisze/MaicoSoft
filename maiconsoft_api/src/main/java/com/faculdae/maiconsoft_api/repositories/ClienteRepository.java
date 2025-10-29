package com.faculdae.maiconsoft_api.repositories;

import com.faculdae.maiconsoft_api.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência da entidade Cliente
 * Suporte para operações CRUD básicas e consultas com Specification
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    
    /**
     * Busca cliente por código único
     * @param codigo Código único do cliente
     * @return Optional contendo o cliente se encontrado
     */
    Optional<Cliente> findByCodigo(String codigo);
    
    /**
     * Verifica se já existe cliente com o código informado
     * @param codigo Código único do cliente
     * @return true se existe, false caso contrário
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Verifica se já existe cliente com CPF/CNPJ informado
     * @param cpfCnpj CPF ou CNPJ do cliente
     * @return true se existe, false caso contrário
     */
    boolean existsByCpfCnpj(String cpfCnpj);
    
    /**
     * Busca cliente por CPF/CNPJ
     * @param cpfCnpj CPF ou CNPJ do cliente
     * @return Optional contendo o cliente se encontrado
     */
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
    
    /**
     * Conta clientes cadastrados entre duas datas
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Número de clientes cadastrados no período
     */
    long countByDatahoraCadastroBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca clientes cadastrados entre duas datas ordenados por data
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Lista de clientes cadastrados no período
     */
    List<Cliente> findByDatahoraCadastroBetweenOrderByDatahoraCadastro(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca os últimos clientes cadastrados
     * @param limit Número máximo de clientes
     * @return Lista dos últimos clientes cadastrados
     */
    @Query(value = "SELECT * FROM CLIENTES ORDER BY datahora_cadastro DESC LIMIT :limit", nativeQuery = true)
    List<Cliente> findTopByOrderByDatahoraCadastroDesc(@Param("limit") int limit);
}