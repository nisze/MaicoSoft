package com.faculdae.maiconsoft_api.repositories;

import com.faculdae.maiconsoft_api.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência da entidade Venda
 * Suporte para operações CRUD básicas e consultas com Specification
 */
@Repository
public interface VendaRepository extends JpaRepository<Venda, Long>, JpaSpecificationExecutor<Venda> {
    
    /**
     * Busca venda por número do orçamento
     * @param numeroOrcamento Número único do orçamento
     * @return Optional contendo a venda se encontrada
     */
    Optional<Venda> findByNumeroOrcamento(String numeroOrcamento);
    
    /**
     * Verifica se já existe venda com o número de orçamento informado
     * @param numeroOrcamento Número único do orçamento
     * @return true se existe, false caso contrário
     */
    boolean existsByNumeroOrcamento(String numeroOrcamento);
    
    /**
     * Busca próximo número sequencial para orçamento do dia
     * @param datePrefix Prefixo da data (YYYYMMDD)
     * @return Próximo número sequencial
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(v.numeroOrcamento, 12) AS integer)), 0) + 1 " +
           "FROM Venda v WHERE v.numeroOrcamento LIKE CONCAT('VND', :datePrefix, '%')")
    Integer findNextSequentialNumber(String datePrefix);
    
    /**
     * Conta vendas por cliente
     * @param clienteId ID do cliente
     * @return Quantidade de vendas do cliente
     */
    @Query("SELECT COUNT(v) FROM Venda v WHERE v.cliente.idCliente = :clienteId")
    Long countByClienteId(Long clienteId);
    
    /**
     * Busca último número de orçamento por mês
     * @param prefixo Prefixo no formato AAAAMM (Ex: 202412)
     * @return Último número sequencial gerado no mês
     */
    @Query(value = "SELECT numero_orcamento FROM VENDAS " +
           "WHERE numero_orcamento LIKE :prefixo || '%' " +
           "ORDER BY CAST(SUBSTRING(numero_orcamento, 7, 3) AS int) DESC LIMIT 1", 
           nativeQuery = true)
    Optional<String> findUltimoNumeroOrcamentoPorMes(@Param("prefixo") String prefixo);
    
    // Métodos para Dashboard
    
    /**
     * Soma o valor total de todas as vendas
     * @return Soma total das vendas
     */
    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v")
    BigDecimal sumTotalValue();
    
    /**
     * Soma o valor das vendas em um período específico
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Soma das vendas no período
     */
    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v WHERE v.dataVenda BETWEEN :startDate AND :endDate")
    BigDecimal sumValueByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Conta vendas entre duas datas
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Número de vendas no período
     */
    long countByDataVendaBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Busca vendas entre duas datas ordenadas por data
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Lista de vendas no período
     */
    List<Venda> findByDataVendaBetweenOrderByDataVenda(LocalDate startDate, LocalDate endDate);
    
    /**
     * Busca as últimas vendas
     * @param limit Número máximo de vendas
     * @return Lista das últimas vendas
     */
    @Query(value = "SELECT * FROM VENDAS ORDER BY data_venda DESC LIMIT :limit", nativeQuery = true)
    List<Venda> findTopByOrderByDataVendaDesc(@Param("limit") int limit);
    
    /**
     * Busca top clientes por receita em um período
     * @param startDate Data inicial
     * @param endDate Data final
     * @param limit Número máximo de clientes
     * @return Lista com nome do cliente, quantidade de vendas e valor total
     */
    @Query(value = "SELECT c.nome, COUNT(v.id) as total_vendas, SUM(v.valor_total) as valor_total " +
           "FROM VENDAS v " +
           "INNER JOIN CLIENTES c ON v.cliente_id = c.id_cliente " +
           "WHERE v.data_venda BETWEEN :startDate AND :endDate " +
           "GROUP BY c.id_cliente, c.nome " +
           "ORDER BY SUM(v.valor_total) DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopClientsByRevenue(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate, 
                                         @Param("limit") int limit);
    
    /**
     * Busca performance dos usuários em um período
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Lista com nome do usuário, quantidade de vendas e valor total
     */
    @Query(value = "SELECT u.nome, COUNT(v.id) as total_vendas, SUM(v.valor_total) as valor_total " +
           "FROM VENDAS v " +
           "INNER JOIN USERS u ON v.usuario_id = u.id " +
           "WHERE v.data_venda BETWEEN :startDate AND :endDate " +
           "GROUP BY u.id, u.nome " +
           "ORDER BY SUM(v.valor_total) DESC", nativeQuery = true)
    List<Object[]> findUserPerformance(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
}