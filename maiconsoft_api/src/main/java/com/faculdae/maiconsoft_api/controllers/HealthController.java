package com.faculdae.maiconsoft_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para health checks do sistema
 * Usado pelo Docker para verificar a saúde da aplicação
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Health Check", description = "Endpoints para verificação da saúde da aplicação")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Verifica se a aplicação está funcionando corretamente")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar se a aplicação está funcionando
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("application", "Maiconsoft API");
            response.put("version", "1.0.0");
            
            // Verificar conexão com o banco de dados
            Map<String, Object> database = new HashMap<>();
            try (Connection connection = dataSource.getConnection()) {
                database.put("status", "UP");
                database.put("database", connection.getMetaData().getDatabaseProductName());
                database.put("url", connection.getMetaData().getURL());
            } catch (Exception e) {
                database.put("status", "DOWN");
                database.put("error", e.getMessage());
                response.put("status", "DEGRADED");
            }
            response.put("database", database);
            
            // Informações do sistema
            Map<String, Object> system = new HashMap<>();
            Runtime runtime = Runtime.getRuntime();
            system.put("totalMemory", runtime.totalMemory());
            system.put("freeMemory", runtime.freeMemory());
            system.put("maxMemory", runtime.maxMemory());
            system.put("processors", runtime.availableProcessors());
            response.put("system", system);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping", description = "Endpoint simples para verificar se a API está respondendo")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.ok(response);
    }
}