package com.faculdae.maiconsoft_api.dto.cliente;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO para requisições de criação/atualização de cliente
 * Contém validações Bean Validation
 */
@Builder
public record ClienteRequestDTO(
        
        @NotBlank(message = "Código é obrigatório")
        @Size(max = 10, message = "Código deve ter no máximo 10 caracteres")
        String codigo,
        
        @NotBlank(message = "Loja é obrigatória")
        @Size(max = 5, message = "Loja deve ter no máximo 5 caracteres")
        String loja,
        
        @NotBlank(message = "Razão social é obrigatória")
        @Size(max = 150, message = "Razão social deve ter no máximo 150 caracteres")
        String razaoSocial,
        
        @NotBlank(message = "Tipo é obrigatório (F=Física, J=Jurídica)")
        @Pattern(regexp = "[FJ]", message = "Tipo deve ser F (Física) ou J (Jurídica)")
        String tipo,
        
        @Size(max = 150, message = "Nome fantasia deve ter no máximo 150 caracteres")
        String nomeFantasia,
        
        @NotBlank(message = "Finalidade é obrigatória (C=Consumidor, R=Revenda)")
        @Pattern(regexp = "[CR]", message = "Finalidade deve ser C (Consumidor) ou R (Revenda)")
        String finalidade,
        
        @NotBlank(message = "CPF/CNPJ é obrigatório")
        @Size(max = 14, message = "CPF/CNPJ deve ter no máximo 14 caracteres")
        String cpfCnpj,
        
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato 00000-000")
        String cep,
        
        @Size(max = 50, message = "País deve ter no máximo 50 caracteres")
        String pais,
        
        @Size(max = 2, message = "Estado deve ter no máximo 2 caracteres")
        String estado,
        
        @Size(max = 20, message = "Código do município deve ter no máximo 20 caracteres")
        String codMunicipio,
        
        @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
        String cidade,
        
        @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
        String endereco,
        
        @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
        String bairro,
        
        @Pattern(regexp = "\\d{2,5}", message = "DDD deve conter apenas números")
        String ddd,
        
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,
        
        @PastOrPresent(message = "Data de abertura não pode ser futura")
        LocalDate abertura,
        
        @Size(max = 100, message = "Contato deve ter no máximo 100 caracteres")
        String contato,
        
        @Email(message = "Email deve ter formato válido")
        @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        String email,
        
        @Size(max = 100, message = "Homepage deve ter no máximo 100 caracteres")
        String homepage,
        
        @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
        String descricao
) {
}