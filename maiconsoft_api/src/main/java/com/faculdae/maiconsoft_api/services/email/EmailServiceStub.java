package com.faculdae.maiconsoft_api.services.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Implementacao stub do EmailService quando email está desabilitado
 * Apenas loga as tentativas de envio sem realmente enviar
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "app.email.enabled", havingValue = "false", matchIfMissing = true)
public class EmailServiceStub implements IEmailService {

    /**
     * Simula envio de notificacao de nova venda para o diretor
     */
    @Override
    public void enviarNotificacaoNovaVenda(String nomeVendedor, String nomeCliente, Double valor) {
        log.info("EMAIL STUB: Nova venda - Vendedor: {}, Cliente: {}, Valor: R$ {}", 
                nomeVendedor, nomeCliente, valor);
    }

    /**
     * Simula envio de email de confirmacao de compra para o cliente
     */
    @Override
    public void enviarConfirmacaoCompra(String emailCliente, String nomeCliente, Double valor) {
        log.info("EMAIL STUB: Confirmacao para {} ({}): R$ {}", 
                nomeCliente, emailCliente, valor);
    }

    /**
     * Simula envio de email de boas-vindas para novo cliente
     */
    @Override
    public void enviarBoasVindasCliente(String emailCliente, String nomeCliente, String codigoCliente) {
        log.info("EMAIL STUB: Email de boas-vindas HTML seria enviado para:");
        log.info("  ├─ Destinatário: {} ({})", nomeCliente, emailCliente);
        log.info("  ├─ Código Cliente: {}", codigoCliente);
        log.info("  ├─ Template: email/cliente-welcome.html");
        log.info("  ├─ Assunto: Bem-vindo à Maiconsoft! - Cadastro Realizado");
        log.info("  └─ Conteúdo: Email HTML profissional com logo MS e design responsivo");
    }

    /**
     * Simula envio de email generico
     */
    @Override
    public void enviarEmail(String para, String assunto, String corpo) {
        log.info("EMAIL STUB: Para: {}, Assunto: {}, Corpo: {}", 
                para, assunto, corpo.substring(0, Math.min(corpo.length(), 100)));
    }

    /**
     * Simula envio de token para reset de senha
     */
    @Override
    public void enviarTokenResetSenha(String emailUsuario, String nomeUsuario, String token) {
        log.info("EMAIL STUB: Token de reset seria enviado para:");
        log.info("  ├─ Destinatário: {} ({})", nomeUsuario, emailUsuario);
        log.info("  ├─ Token: {}", token);
        log.info("  ├─ Validade: 15 minutos");
        log.info("  └─ Assunto: Reset de Senha - Maiconsoft");
    }
}