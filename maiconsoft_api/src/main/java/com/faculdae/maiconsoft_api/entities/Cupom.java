package com.faculdae.maiconsoft_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CUPOM")
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CUPOM")
    private Long idCupom;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "NOME", length = 100)
    private String nome;

    @Column(name = "DESCRICAO", length = 150)
    private String descricao;

    @Column(name = "DESCONTO_PERCENTUAL")
    private Double descontoPercentual;

    @Column(name = "DESCONTO_VALOR")
    private Double descontoValor;

    @Column(name = "VALIDADE")
    private LocalDate validade;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "VALOR_MINIMO")
    private Double valorMinimo;

    @Column(name = "MAX_USOS")
    private Integer maxUsos;

    @Column(name = "USOS_ATUAL")
    private Integer usosAtual;

    /**
     * Alias para descontoPercentual para compatibilidade
     */
    public Double getPorcentagem() {
        return descontoPercentual;
    }
}