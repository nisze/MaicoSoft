package com.faculdae.maiconsoft_api.services.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Serviço de envio de emails
 * Configurável via properties (pode ser desabilitado em dev/test)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.email.enabled", havingValue = "true", matchIfMissing = false)
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from:noreply@maiconsoft.com}")
    private String fromEmail;

    @Value("${app.email.diretor:diretor@maiconsoft.com}")
    private String diretorEmail;

    /**
     * Envia notificação de nova venda para o diretor
     */
    @Override
    public void enviarNotificacaoNovaVenda(String nomeVendedor, String nomeCliente, Double valor) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(diretorEmail);
            message.setSubject("Nova Venda Realizada - Maiconsoft");
            message.setText(String.format(
                "Uma nova venda foi realizada:\n\n" +
                "Vendedor: %s\n" +
                "Cliente: %s\n" +
                "Valor: R$ %.2f\n\n" +
                "Sistema Maiconsoft",
                nomeVendedor, nomeCliente, valor
            ));

            mailSender.send(message);
            log.info("Email de nova venda enviado com sucesso para: {}", diretorEmail);
        } catch (Exception e) {
            log.error("Erro ao enviar email de nova venda: {}", e.getMessage(), e);
        }
    }

    /**
     * Envia email de confirmação de compra para o cliente
     */
    @Override
    public void enviarConfirmacaoCompra(String emailCliente, String nomeCliente, Double valor) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailCliente);
            message.setSubject("Confirmação de Compra - Maiconsoft");
            message.setText(String.format(
                "Olá %s,\n\n" +
                "Sua compra no valor de R$ %.2f foi confirmada com sucesso.\n\n" +
                "Obrigado por escolher a Maiconsoft!\n\n" +
                "Atenciosamente,\n" +
                "Equipe Maiconsoft",
                nomeCliente, valor
            ));

            mailSender.send(message);
            log.info("Email de confirmação enviado com sucesso para: {}", emailCliente);
        } catch (Exception e) {
            log.error("Erro ao enviar email de confirmação: {}", e.getMessage(), e);
        }
    }

    /**
     * Envia email de boas-vindas para novo cliente cadastrado
     */
    @Override
    public void enviarBoasVindasCliente(String emailCliente, String nomeCliente, String codigoCliente) {
        try {
            // Preparar contexto para o template Thymeleaf
            Context context = new Context();
            context.setVariable("nomeCliente", nomeCliente);
            context.setVariable("codigoCliente", codigoCliente);
            
            // Processar template HTML
            String htmlContent = templateEngine.process("email/cliente-welcome", context);
            
            // Criar mensagem MIME para HTML
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(emailCliente);
            helper.setSubject("Bem-vindo à Maiconsoft! - Cadastro Realizado");
            helper.setText(htmlContent, true); // true = HTML content
            
            mailSender.send(mimeMessage);
            log.info("Email de boas-vindas HTML enviado com sucesso para cliente {} ({}): {}", 
                    nomeCliente, codigoCliente, emailCliente);
                    
        } catch (MessagingException e) {
            log.error("Erro ao criar mensagem HTML para cliente {} ({}): {}", 
                    nomeCliente, codigoCliente, e.getMessage(), e);
            // Fallback: tentar enviar email de texto simples
            enviarBoasVindasClienteTextoSimples(emailCliente, nomeCliente, codigoCliente);
        } catch (Exception e) {
            log.error("Erro ao enviar email de boas-vindas para cliente {} ({}): {}", 
                    nomeCliente, codigoCliente, e.getMessage(), e);
        }
    }
    
    /**
     * Método de fallback para envio de email em texto simples
     */
    private void enviarBoasVindasClienteTextoSimples(String emailCliente, String nomeCliente, String codigoCliente) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailCliente);
            message.setSubject("Bem-vindo à Maiconsoft! - Cadastro Realizado");
            message.setText(String.format(
                "Olá %s,\n\n" +
                "É com grande satisfação que confirmamos seu cadastro em nossa plataforma!\n\n" +
                "Seus dados foram registrados com sucesso\n" +
                "Seu código de cliente: %s\n" +
                "Você já está pronto para realizar suas compras\n\n" +
                "Nossa equipe está à disposição para atendê-lo e oferecer as melhores soluções " +
                "em tecnologia e desenvolvimento de sistemas.\n\n" +
                "Principais serviços disponíveis:\n" +
                "• Desenvolvimento de Sistemas\n" +
                "• Consultoria em TI\n" +
                "• Suporte Técnico\n" +
                "• Design e Marketing Digital\n\n" +
                "Em breve, entraremos em contato para apresentar nossas soluções personalizadas " +
                "para suas necessidades.\n\n" +
                "Obrigado por confiar na Maiconsoft!\n\n" +
                "Atenciosamente,\n" +
                "Equipe Maiconsoft\n" +
                "Telefone: (11) 99999-9999\n" +
                "Email: contato@maiconsoft.com\n" +
                "Site: www.maiconsoft.com",
                nomeCliente, codigoCliente
            ));

            mailSender.send(message);
            log.info("Email de boas-vindas (texto simples) enviado como fallback para cliente {} ({}): {}", 
                    nomeCliente, codigoCliente, emailCliente);
        } catch (Exception fallbackError) {
            log.error("Erro crítico: Não foi possível enviar email nem em HTML nem em texto para cliente {} ({}): {}", 
                    nomeCliente, codigoCliente, fallbackError.getMessage(), fallbackError);
        }
    }

    /**
     * Envia email genérico
     */
    @Override
    public void enviarEmail(String para, String assunto, String corpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(para);
            message.setSubject(assunto);
            message.setText(corpo);

            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", para);
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", para, e.getMessage(), e);
        }
    }

    /**
     * Envia token para reset de senha
     */
    @Override
    public void enviarTokenResetSenha(String emailUsuario, String nomeUsuario, String token) {
        try {
            Context context = new Context();
            context.setVariable("nomeUsuario", nomeUsuario);
            context.setVariable("token", token);

            String htmlContent = templateEngine.process("email/reset-senha", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailUsuario);
            helper.setSubject("Reset de Senha - Maiconsoft");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Email de reset de senha enviado para: {}", emailUsuario);
        } catch (MessagingException e) {
            log.error("Erro ao enviar email de reset para {}: {}", emailUsuario, e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar email de reset de senha", e);
        }
    }
}