package com.faculdae.maiconsoft_api.repositories;

import com.faculdae.maiconsoft_api.entities.DashboardLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações de persistência da entidade DashboardLog
 * Suporte para operações de auditoria e logs estruturados
 */
@Repository
public interface DashboardLogRepository extends JpaRepository<DashboardLog, Long> {

    /**
     * Busca logs por usuário
     * @param usuarioId ID do usuário
     * @return Lista de logs
     */
    List<DashboardLog> findByUsuarioIdOrderByDataHoraDesc(String usuarioId);

    /**
     * Busca logs por período
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de logs
     */
    List<DashboardLog> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca logs por ação
     * @param acao Ação realizada
     * @return Lista de logs
     */
    List<DashboardLog> findByAcaoOrderByDataHoraDesc(String acao);

    /**
     * Busca logs por entidade
     * @param entidade Nome da entidade
     * @param entidadeId ID da entidade
     * @return Lista de logs
     */
    List<DashboardLog> findByEntidadeAndEntidadeIdOrderByDataHoraDesc(String entidade, Long entidadeId);

    /**
     * Conta logs por usuário em um período
     * @param usuarioId ID do usuário
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Quantidade de logs
     */
    @Query("SELECT COUNT(d) FROM DashboardLog d WHERE d.usuarioId = :usuarioId " +
           "AND d.dataHora BETWEEN :dataInicio AND :dataFim")
    Long countByUsuarioAndPeriodo(@Param("usuarioId") String usuarioId,
                                  @Param("dataInicio") LocalDateTime dataInicio,
                                  @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca logs mais recentes
     * @param limit Quantidade máxima
     * @return Lista de logs
     */
    @Query("SELECT d FROM DashboardLog d ORDER BY d.dataHora DESC")
    List<DashboardLog> findRecentLogs(@Param("limit") int limit);
}