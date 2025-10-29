package com.faculdae.maiconsoft_api.services.audit;

import com.faculdae.maiconsoft_api.entities.DashboardLog;
import com.faculdae.maiconsoft_api.repositories.DashboardLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Service para auditoria e logs estruturados
 * Utiliza a entidade DashboardLog para armazenamento
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final DashboardLogRepository dashboardLogRepository;

    /**
     * Registra log de auditoria de forma assíncrona
     * @param action Ação realizada
     * @param entity Entidade afetada
     * @param entityId ID da entidade
     * @param details Detalhes adicionais
     * @return CompletableFuture do log criado
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<DashboardLog> logUserAction(String action, String entity, 
                                                        Long entityId, String details) {
        try {
            String userCode = getCurrentUserCode();
            
            DashboardLog logEntry = DashboardLog.builder()
                    .dataHora(LocalDateTime.now())
                    .acao(action)
                    .entidade(entity)
                    .entidadeId(entityId)
                    .usuarioId(userCode)
                    .descricao(details)
                    .ipOrigem(getClientIpAddress())
                    .userAgent(getUserAgent())
                    .build();
            
            DashboardLog savedLog = dashboardLogRepository.save(logEntry);
            
            log.info("Audit log created - Action: {}, Entity: {}, ID: {}, User: {}", 
                    action, entity, entityId, userCode);
            
            return CompletableFuture.completedFuture(savedLog);
        } catch (Exception e) {
            log.error("Error creating audit log: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Log de operações CRUD
     */
    @Async
    public CompletableFuture<Void> logCrudOperation(String operation, String entityName, 
                                                   Long entityId, Object entityData) {
        String details = String.format("Operation: %s on %s (ID: %d)", 
                operation, entityName, entityId);
        
        if (entityData != null) {
            details += " - Data: " + entityData.toString();
        }
        
        return logUserAction(operation, entityName, entityId, details)
                .thenRun(() -> log.debug("CRUD operation logged: {}", operation));
    }

    /**
     * Log de integrações externas
     */
    @Async
    public CompletableFuture<Void> logExternalIntegration(String service, String operation, 
                                                         String request, String response, 
                                                         boolean success) {
        String action = String.format("EXTERNAL_%s_%s", service.toUpperCase(), operation.toUpperCase());
        String details = String.format("Service: %s, Success: %s, Request: %s, Response: %s", 
                service, success, truncate(request, 500), truncate(response, 500));
        
        return logUserAction(action, "EXTERNAL_SERVICE", null, details)
                .thenRun(() -> log.info("External integration logged: {} - Success: {}", service, success));
    }

    /**
     * Log de autenticação e autorização
     */
    @Async
    public CompletableFuture<Void> logAuthEvent(String event, String username, boolean success, 
                                               String details) {
        String action = String.format("AUTH_%s", event.toUpperCase());
        String logDetails = String.format("Event: %s, User: %s, Success: %s, Details: %s", 
                event, username, success, details);
        
        return logUserAction(action, "USER", null, logDetails)
                .thenRun(() -> log.info("Auth event logged: {} - User: {} - Success: {}", 
                        event, username, success));
    }

    /**
     * Log de erros e exceções
     */
    @Async
    public CompletableFuture<Void> logError(String errorType, String message, String stackTrace, 
                                           String context) {
        String details = String.format("Type: %s, Message: %s, Context: %s, Stack: %s", 
                errorType, message, context, truncate(stackTrace, 1000));
        
        return logUserAction("ERROR", "SYSTEM", null, details)
                .thenRun(() -> log.error("Error logged: {} - {}", errorType, message));
    }

    /**
     * Log de performance e monitoring
     */
    @Async
    public CompletableFuture<Void> logPerformanceMetric(String operation, long duration, 
                                                        String additionalInfo) {
        String details = String.format("Operation: %s, Duration: %dms, Info: %s", 
                operation, duration, additionalInfo);
        
        return logUserAction("PERFORMANCE", "METRIC", null, details)
                .thenRun(() -> log.info("Performance metric logged: {} - {}ms", operation, duration));
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Obtém código do usuário atual (sem autenticação)
     */
    private String getCurrentUserCode() {
        return "ADMIN"; // Usuário padrão sem autenticação
    }

    /**
     * Obtém IP do cliente (placeholder - implementar com RequestContextHolder se necessário)
     */
    private String getClientIpAddress() {
        // TODO: Implementar obtenção de IP real do request
        return "127.0.0.1";
    }

    /**
     * Obtém User-Agent (placeholder)
     */
    private String getUserAgent() {
        // TODO: Implementar obtenção de User-Agent real do request
        return "Maiconsoft-API-Client";
    }

    /**
     * Trunca string para tamanho máximo
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return null;
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}