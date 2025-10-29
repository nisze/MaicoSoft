package com.faculdae.maiconsoft_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8090}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development Server");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Maiconsoft API")
                        .description("API do Sistema Maiconsoft com sistema de códigos de acesso automático")
                        .version("1.0.0"));
    }
}