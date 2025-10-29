package com.faculdae.maiconsoft_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAGAMENTOS")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGAMENTO")
    private Long idPagamento;

    @ManyToOne
    @JoinColumn(name = "ID_VENDA", nullable = false)
    private Venda venda;

    @Column(name = "FORMA_PAGAMENTO", nullable = false, length = 50)
    private String formaPagamento;

    @Column(name = "VALOR", nullable = false)
    private Double valor;

    @Column(name = "DATA_PAGAMENTO")
    private LocalDateTime dataPagamento;
}

