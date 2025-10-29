package com.faculdae.maiconsoft_api.dto.cliente;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de dados do cliente
 * Usado em listagens e consultas individuais
 */
@Builder
@Getter
@Setter
public class ClienteResponseDTO {
    
    private Long idCliente;
    private String codigo;
    private String loja;
    private String razaoSocial;
    private String tipo;
    private String nomeFantasia;
    private String finalidade;
    private String cpfCnpj;
    private String cep;
    private String pais;
    private String estado;
    private String codMunicipio;
    private String cidade;
    private String endereco;
    private String bairro;
    private String ddd;
    private String telefone;
    private LocalDate abertura;
    private String contato;
    private String email;
    private String homepage;
    private LocalDateTime datahoraCadastro;
    private String descricao;
    private String usuarioCadastroNome;
    private String usuarioCadastroCodigo;
}