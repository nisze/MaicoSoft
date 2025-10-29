package com.faculdae.maiconsoft_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "VENDAS")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VENDA")
    private Long idVenda;

    @Column(name = "NUMERO_ORCAMENTO", nullable = false, length = 20)
    private String numeroOrcamento;

    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "ID_CUPOM")
    private Cupom cupom;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "VALOR_BRUTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBruto;

    @Column(name = "VALOR_DESCONTO", precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "VALOR_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "DATA_VENDA")
    private LocalDate dataVenda;

    @Column(name = "DATAHORA_CADASTRO")
    private LocalDateTime datahoraCadastro;

    @Column(name = "OBSERVACAO", columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_CADASTRO")
    private User usuarioCadastro;
}