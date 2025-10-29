package com.faculdae.maiconsoft_api.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DASHBOARD_LOG")
public class DashboardLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOG")
    private Long idLog;

    @Column(name = "USUARIO_ID", length = 50)
    private String usuarioId;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private User usuario;

    @Column(name = "ACAO", nullable = false, length = 100)
    private String acao;

    @Column(name = "DESCRICAO", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "ENTIDADE", length = 50)
    private String entidade;

    @Column(name = "ENTIDADE_ID")
    private Long entidadeId;

    @Column(name = "DATA_HORA")
    private LocalDateTime dataHora;

    @Column(name = "IP_ORIGEM", length = 45)
    private String ipOrigem;

    @Column(name = "USER_AGENT", length = 500)
    private String userAgent;

    @Column(name = "METADADOS", columnDefinition = "TEXT")
    private String metadados;
}