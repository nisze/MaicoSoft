package com.faculdae.maiconsoft_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate para consumo de APIs externas
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Bean do RestTemplate com configurações de timeout
     * Timeout configurado para 3 segundos conforme requisitos
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3 segundos para conectar
        factory.setReadTimeout(3000);    // 3 segundos para ler resposta
        
        return new RestTemplate(factory);
    }
}