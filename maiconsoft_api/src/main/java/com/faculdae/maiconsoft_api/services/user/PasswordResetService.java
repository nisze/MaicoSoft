package com.faculdae.maiconsoft_api.services.user;

import com.faculdae.maiconsoft_api.entities.User;
import com.faculdae.maiconsoft_api.repositories.UserRepository;
import com.faculdae.maiconsoft_api.services.email.IEmailService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

/**
 * Serviço para gerenciar reset de senha
 * Usa Caffeine para cache temporário de tokens (15 minutos)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Cache de tokens: key = token, value = codigoAcesso
    private final Cache<String, String> resetTokenCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(15))
            .maximumSize(1000)
            .build();

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    /**
     * Solicita reset de senha - gera token e envia por email
     * Aceita código de acesso OU email
     */
    public boolean solicitarResetSenha(String identificador) {
        log.info("🔄 Iniciando processo de reset de senha para: {}", identificador);
        
        // Tenta buscar por código de acesso primeiro
        Optional<User> userOpt = userRepository.findByCodigoAcesso(identificador);
        
        // Se não encontrou, tenta buscar por email
        if (userOpt.isEmpty()) {
            log.info("🔍 Não encontrado como código de acesso, tentando como email...");
            userOpt = userRepository.findByEmail(identificador);
        }
        
        if (userOpt.isEmpty()) {
            log.warn("❌ Usuário não encontrado para: {}", identificador);
            return false;
        }

        User user = userOpt.get();
        log.info("✅ Usuário encontrado: {} (Código: {}, Email: {})", 
                user.getNome(), user.getCodigoAcesso(), user.getEmail());
        
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("❌ Usuário {} não possui email cadastrado", user.getCodigoAcesso());
            return false;
        }

        // Gera token
        String token = gerarToken();
        
        // Armazena no cache usando código de acesso como chave
        resetTokenCache.put(token, user.getCodigoAcesso());
        
        log.info("🔑 Token de reset gerado para usuário: {} - Token: {} - Email: {}", 
                user.getCodigoAcesso(), token, user.getEmail());

        // Envia email
        try {
            log.info("📧 Tentando enviar email para: {}", user.getEmail());
            emailService.enviarTokenResetSenha(user.getEmail(), user.getNome(), token);
            log.info("✅ Email enviado com sucesso!");
            return true;
        } catch (Exception e) {
            log.error("❌ Erro ao enviar email de reset para {}", user.getEmail(), e);
            resetTokenCache.invalidate(token); // Remove token do cache em caso de erro
            return false;
        }
    }

    /**
     * Valida o token e retorna o código de acesso associado
     */
    public Optional<String> validarToken(String token) {
        String codigoAcesso = resetTokenCache.getIfPresent(token);
        if (codigoAcesso != null) {
            log.info("Token válido encontrado para: {}", codigoAcesso);
            return Optional.of(codigoAcesso);
        }
        log.warn("Token inválido ou expirado: {}", token);
        return Optional.empty();
    }

    /**
     * Reseta a senha usando o token
     */
    public boolean resetarSenha(String token, String novaSenha) {
        Optional<String> codigoAcessoOpt = validarToken(token);
        
        if (codigoAcessoOpt.isEmpty()) {
            return false;
        }

        String codigoAcesso = codigoAcessoOpt.get();
        Optional<User> userOpt = userRepository.findByCodigoAcesso(codigoAcesso);
        
        if (userOpt.isEmpty()) {
            log.error("Usuário não encontrado após validação de token: {}", codigoAcesso);
            return false;
        }

        User user = userOpt.get();
        user.setSenha(passwordEncoder.encode(novaSenha));
        userRepository.save(user);
        
        // Remove token do cache após uso
        resetTokenCache.invalidate(token);
        
        log.info("Senha resetada com sucesso para usuário: {}", codigoAcesso);
        return true;
    }

    /**
     * Gera token alfanumérico aleatório
     */
    private String gerarToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }

    /**
     * Verifica quantos tokens estão ativos no cache
     */
    public long getTokensAtivos() {
        return resetTokenCache.estimatedSize();
    }
}