package com.faculdae.maiconsoft_api.services;

import com.faculdae.maiconsoft_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CodigoAcessoService {

    @Autowired
    private UserRepository userRepository;

    // Caracteres permitidos no código de acesso (letras + números)
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TAMANHO_CODIGO = 6;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Gera um código de acesso único e aleatório
     * @return código de acesso único
     */
    public String gerarCodigoUnico() {
        String codigo;
        int tentativas = 0;
        final int MAX_TENTATIVAS = 100;

        do {
            codigo = gerarCodigoAleatorio();
            tentativas++;
            
            if (tentativas > MAX_TENTATIVAS) {
                throw new RuntimeException("Não foi possível gerar um código único após " + MAX_TENTATIVAS + " tentativas");
            }
            
        } while (userRepository.existsByCodigoAcessoIgnoreCase(codigo));

        return codigo;
    }

    /**
     * Gera um código aleatório de 6 caracteres
     * @return código aleatório
     */
    private String gerarCodigoAleatorio() {
        StringBuilder codigo = new StringBuilder(TAMANHO_CODIGO);
        
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            int indice = random.nextInt(CARACTERES.length());
            codigo.append(CARACTERES.charAt(indice));
        }
        
        return codigo.toString();
    }

    /**
     * Valida se um código tem o formato correto
     * @param codigo código a ser validado
     * @return true se válido
     */
    public boolean isCodigoValido(String codigo) {
        if (codigo == null || codigo.length() != TAMANHO_CODIGO) {
            return false;
        }
        
        return codigo.toUpperCase().matches("^[A-Z0-9]{" + TAMANHO_CODIGO + "}$");
    }

    /**
     * Normaliza o código para uppercase para comparações case-insensitive
     * @param codigo código a ser normalizado
     * @return código em uppercase
     */
    public String normalizarCodigo(String codigo) {
        return codigo != null ? codigo.toUpperCase().trim() : null;
    }
}