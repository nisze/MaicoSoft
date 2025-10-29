package com.faculdae.maiconsoft_api.dto.cupom;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CupomRequestDTO {

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 20, message = "Código deve ter no máximo 20 caracteres")
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 150, message = "Descrição deve ter no máximo 150 caracteres")
    private String descricao;

    @DecimalMin(value = "0.0", inclusive = false, message = "Desconto percentual deve ser maior que 0")
    @DecimalMax(value = "100.0", message = "Desconto percentual deve ser no máximo 100%")
    private Double descontoPercentual;

    @DecimalMin(value = "0.0", inclusive = false, message = "Desconto em valor deve ser maior que 0")
    private Double descontoValor;

    @Future(message = "Data de validade deve ser no futuro")
    private LocalDate validade;

    @NotBlank(message = "Status é obrigatório")
    @Pattern(regexp = "ATIVO|INATIVO", message = "Status deve ser ATIVO ou INATIVO")
    private String status;

    @DecimalMin(value = "0.0", message = "Valor mínimo deve ser maior ou igual a 0")
    private Double valorMinimo;

    @Min(value = 1, message = "Máximo de usos deve ser pelo menos 1")
    private Integer maxUsos;

    @Min(value = 0, message = "Usos atuais deve ser maior ou igual a 0")
    private Integer usosAtuais = 0;
}