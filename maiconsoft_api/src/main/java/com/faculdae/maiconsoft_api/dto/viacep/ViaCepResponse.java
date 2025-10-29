package com.faculdae.maiconsoft_api.dto.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta da API ViaCEP
 * Mapeamento dos campos retornados pela API externa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViaCepResponse {
    
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String ibge;
    private String gia;
    private String ddd;
    private String siafi;
    private Boolean erro; // Campo especial da API que indica CEP inválido
    
    /**
     * Verifica se a resposta indica um CEP inválido
     * @return true se CEP é inválido
     */
    public boolean isErro() {
        return erro != null && erro;
    }
    
    /**
     * Verifica se a resposta contém dados válidos
     * @return true se contém dados válidos do CEP
     */
    public boolean isValid() {
        return !isErro() && cep != null && !cep.trim().isEmpty();
    }
}