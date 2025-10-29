package com.faculdae.maiconsoft_api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro de Rate Limiting simples baseado em IP
 */
@Component
@ConditionalOnProperty(value = "app.rate-limit.enabled", havingValue = "true", matchIfMissing = false)
@Order(1)
@Slf4j
public class RateLimitFilter implements Filter {

    @Value("${app.rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;

    @Value("${app.rate-limit.burst-capacity:150}")
    private int burstCapacity;

    // Cache para armazenar contadores por IP
    private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    // Classe interna para contar requests
    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private LocalDateTime lastReset = LocalDateTime.now();
        
        public synchronized int incrementAndGet() {
            LocalDateTime now = LocalDateTime.now();
            
            // Reset contador se passou 1 minuto
            if (ChronoUnit.MINUTES.between(lastReset, now) >= 1) {
                count.set(0);
                lastReset = now;
            }
            
            return count.incrementAndGet();
        }
        
        public int getCurrentCount() {
            return count.get();
        }
        
        public LocalDateTime getLastReset() {
            return lastReset;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Rate Limiting Filter initialized - Rate: {} req/min, Burst: {}", 
                requestsPerMinute, burstCapacity);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientIp = getClientIpAddress(httpRequest);
        String requestUri = httpRequest.getRequestURI();
        
        // Pular rate limiting para endpoints de saúde e estáticos
        if (shouldSkipRateLimit(requestUri)) {
            chain.doFilter(request, response);
            return;
        }
        
        RequestCounter counter = requestCounters.computeIfAbsent(clientIp, k -> new RequestCounter());
        int currentCount = counter.incrementAndGet();
        
        // Verificar se excedeu o limite
        if (currentCount > burstCapacity) {
            // Rate limit excedido
            log.warn("Rate limit exceeded for IP: {} - Count: {} - URI: {}", clientIp, currentCount, requestUri);
            
            httpResponse.setStatus(429); // HTTP 429 Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
            httpResponse.setHeader("X-RateLimit-Remaining", "0");
            httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000));
            httpResponse.setHeader("Retry-After", "60");
            
            String errorResponse = """
                {
                    "timestamp": "%s",
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "Rate limit exceeded. Maximum %d requests per minute allowed.",
                    "path": "%s"
                }
                """.formatted(LocalDateTime.now().toString(), requestsPerMinute, requestUri);
            
            httpResponse.getWriter().write(errorResponse);
            return;
        }
        
        // Adicionar headers informativos
        int remaining = Math.max(0, requestsPerMinute - currentCount);
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(counter.getLastReset().plus(1, ChronoUnit.MINUTES)));
        
        // Log se está próximo do limite (90%)
        if (currentCount > (requestsPerMinute * 0.9)) {
            log.info("Rate limit warning for IP: {} - Count: {}/{} - URI: {}", 
                    clientIp, currentCount, requestsPerMinute, requestUri);
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        requestCounters.clear();
        log.info("Rate Limiting Filter destroyed");
    }

    /**
     * Obtém o IP real do cliente considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Pegar primeiro IP se houver múltiplos
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Define URIs que devem pular o rate limiting
     */
    private boolean shouldSkipRateLimit(String uri) {
        String[] skipPatterns = {
            "/actuator/health",
            "/actuator/info",
            "/favicon.ico",
            "/swagger-ui",
            "/v3/api-docs",
            "/webjars/",
            "/css/",
            "/js/",
            "/images/",
            "/static/"
        };
        
        for (String pattern : skipPatterns) {
            if (uri.startsWith(pattern)) {
                return true;
            }
        }
        
        return false;
    }
}