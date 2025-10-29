package com.faculdae.maiconsoft_api.services.email;

/**
 * Interface para serviços de email
 */
public interface IEmailService {
    
    /**
     * Envia notificação de nova venda para o diretor
     */
    void enviarNotificacaoNovaVenda(String nomeVendedor, String nomeCliente, Double valor);

    /**
     * Envia email de confirmação de compra para o cliente
     */
    void enviarConfirmacaoCompra(String emailCliente, String nomeCliente, Double valor);

    /**
     * Envia email de boas-vindas para novo cliente cadastrado
     */
    void enviarBoasVindasCliente(String emailCliente, String nomeCliente, String codigoCliente);

    /**
     * Envia email genérico
     */
    void enviarEmail(String para, String assunto, String corpo);
}