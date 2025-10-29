package com.faculdae.maiconsoft_api.dto.cupom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CupomResponseDTO {

    private Long idCupom;
    private String codigo;
    private String nome;
    private String descricao;
    private Double descontoPercentual;
    private Double descontoValor;
    private LocalDate validade;
    private String status;
    private Double valorMinimo;
    private Integer maxUsos;
    private Integer usosAtuais;
    private boolean isAtivo;
    private boolean isExpirado;
    private boolean isLimiteEsgotado;
}