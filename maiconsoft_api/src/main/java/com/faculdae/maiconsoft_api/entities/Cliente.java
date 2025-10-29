package com.faculdae.maiconsoft_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CLIENTES")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE")
    private Long idCliente;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "LOJA", nullable = false, length = 5)
    private String loja;

    @Column(name = "RAZAO_SOCIAL", nullable = false, length = 150)
    private String razaoSocial;

    @Column(name = "TIPO", nullable = false, length = 1)
    private String tipo;

    @Column(name = "NOME_FANTASIA", length = 150)
    private String nomeFantasia;

    @Column(name = "FINALIDADE", length = 1)
    private String finalidade;

    @Column(name = "CPF_CNPJ", nullable = false, length = 14)
    private String cpfCnpj;

    @Column(name = "CEP", length = 10)
    private String cep;

    @Column(name = "PAIS", length = 50)
    private String pais;

    @Column(name = "ESTADO", length = 2)
    private String estado;

    @Column(name = "COD_MUNICIPIO", length = 20)
    private String codMunicipio;

    @Column(name = "CIDADE", length = 100)
    private String cidade;

    @Column(name = "ENDERECO", length = 200)
    private String endereco;

    @Column(name = "BAIRRO", length = 100)
    private String bairro;

    @Column(name = "DDD", length = 5)
    private String ddd;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "ABERTURA")
    private LocalDate abertura;

    @Column(name = "CONTATO", length = 100)
    private String contato;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "HOMEPAGE", length = 100)
    private String homepage;

    @Column(name = "DATAHORA_CADASTRO")
    private LocalDateTime datahoraCadastro;

    @Column(name = "DESCRICAO", columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_CADASTRO")
    private User usuarioCadastro;
}
