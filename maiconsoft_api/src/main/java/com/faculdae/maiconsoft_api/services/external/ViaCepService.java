package com.faculdae.maiconsoft_api.services.external;

import com.faculdae.maiconsoft_api.dto.viacep.ViaCepResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Serviço para integração com a API ViaCEP
 * Busca informações de endereço por CEP
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViaCepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";
    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");
    
    private final RestTemplate restTemplate;

    /**
     * Busca informações de endereço por CEP na API ViaCEP
     * @param cep CEP para busca (formato: 00000-000 ou 00000000)
     * @return Optional com dados do endereço se encontrado
     */
    public Optional<ViaCepResponse> buscarCep(String cep) {
        if (!isValidCep(cep)) {
            log.warn("CEP inválido fornecido: {}", cep);
            return Optional.empty();
        }

        String cleanCep = limparCep(cep);
        log.info("Buscando CEP na API ViaCEP: {}", cleanCep);

        try {
            ViaCepResponse response = restTemplate.getForObject(VIACEP_URL, ViaCepResponse.class, cleanCep);
            
            if (response == null) {
                log.warn("Resposta nula da API ViaCEP para CEP: {}", cleanCep);
                return Optional.empty();
            }
            
            if (response.isErro()) {
                log.warn("CEP não encontrado na API ViaCEP: {}", cleanCep);
                return Optional.empty();
            }
            
            if (!response.isValid()) {
                log.warn("Resposta inválida da API ViaCEP para CEP: {}", cleanCep);
                return Optional.empty();
            }
            
            log.info("CEP encontrado com sucesso: {} - {}", cleanCep, response.getLocalidade());
            return Optional.of(response);
            
        } catch (RestClientException e) {
            log.error("Erro ao buscar CEP na API ViaCEP: {} - Erro: {}", cleanCep, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar CEP: {} - Erro: {}", cleanCep, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Valida formato do CEP
     * @param cep CEP para validação
     * @return true se CEP tem formato válido
     */
    public boolean isValidCep(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            return false;
        }
        return CEP_PATTERN.matcher(cep.trim()).matches();
    }

    /**
     * Remove caracteres especiais do CEP, mantendo apenas números
     * @param cep CEP com ou sem formatação
     * @return CEP apenas com números
     */
    private String limparCep(String cep) {
        return cep.replaceAll("[^\\d]", "");
    }

    /**
     * Verifica se o serviço ViaCEP está disponível
     * @return true se serviço está disponível
     */
    public boolean isServiceAvailable() {
        try {
            // Testa com um CEP conhecido válido (Av. Paulista, São Paulo)
            Optional<ViaCepResponse> response = buscarCep("01310-100");
            return response.isPresent();
        } catch (Exception e) {
            log.warn("Serviço ViaCEP indisponível: {}", e.getMessage());
            return false;
        }
    }
}