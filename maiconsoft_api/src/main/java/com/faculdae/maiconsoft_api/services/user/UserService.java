package com.faculdae.maiconsoft_api.services.user;

import com.faculdae.maiconsoft_api.dto.user.UserRequestDTO;
import com.faculdae.maiconsoft_api.dto.LoginRequestDTO;
import com.faculdae.maiconsoft_api.dto.LoginResponseDTO;
import com.faculdae.maiconsoft_api.dto.UserResponseDTO;
import com.faculdae.maiconsoft_api.entities.User;
import com.faculdae.maiconsoft_api.repositories.UserRepository;
import com.faculdae.maiconsoft_api.services.CodigoAcessoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CodigoAcessoService codigoAcessoService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Login simples sem JWT - Case insensitive
     */
    public LoginResponseDTO login(String codigoAcesso, String senha) {
        // Normalizar código para busca case-insensitive
        String codigoNormalizado = codigoAcessoService.normalizarCodigo(codigoAcesso);
        
        User user = userRepository.findByCodigoAcessoIgnoreCaseAndAtivoTrue(codigoNormalizado)
                .orElse(null);
        
        if (user == null) {
            return new LoginResponseDTO(null, null, null, null, false, "Código de acesso não encontrado ou usuário inativo");
        }
        
        if (!passwordEncoder.matches(senha, user.getSenha())) {
            return new LoginResponseDTO(null, null, null, null, false, "Senha incorreta");
        }
        
        return new LoginResponseDTO(
                user.getIdUser(),
                user.getNome(),
                user.getEmail(),
                user.getCodigoAcesso(),
                true,
                "Login realizado com sucesso"
        );
    }

    /**
     * Listar usuários
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Buscar usuário por ID
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    /**
     * Criar usuário com código automático
     */
    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequest) {
        // Validações
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + userRequest.getEmail());
        }
        
        if (userRepository.existsByCpf(userRequest.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado: " + userRequest.getCpf());
        }
        
        // Gerar código de acesso se não fornecido
        String codigoAcesso = userRequest.getCodigoAcesso();
        if (codigoAcesso == null || codigoAcesso.trim().isEmpty()) {
            codigoAcesso = codigoAcessoService.gerarCodigoUnico();
        } else {
            // Normalizar código fornecido
            codigoAcesso = codigoAcessoService.normalizarCodigo(codigoAcesso);
            
            // Validar formato
            if (!codigoAcessoService.isCodigoValido(codigoAcesso)) {
                throw new IllegalArgumentException("Código de acesso deve ter 6 caracteres alfanuméricos");
            }
            
            // Verificar se já existe
            if (userRepository.existsByCodigoAcessoIgnoreCase(codigoAcesso)) {
                throw new IllegalArgumentException("Código de acesso já cadastrado: " + codigoAcesso);
            }
        }

        User user = new User();
        user.setNome(userRequest.getNome());
        user.setEmail(userRequest.getEmail());
        user.setCpf(userRequest.getCpf());
        user.setTelefone(userRequest.getTelefone());
        user.setCodigoAcesso(codigoAcesso);
        user.setSenha(passwordEncoder.encode(userRequest.getSenha()));
        user.setAtivo(userRequest.getAtivo() != null ? userRequest.getAtivo() : true);
        user.setTipoUsuario(userRequest.getTipoUsuario() != null ? userRequest.getTipoUsuario() : "FUNCIONARIO");

        User savedUser = userRepository.save(user);
        
        String mensagem = userRequest.getCodigoAcesso() == null || userRequest.getCodigoAcesso().trim().isEmpty() 
                ? "Usuário criado com sucesso! Use o código " + codigoAcesso + " para fazer login."
                : "Usuário criado com sucesso!";

        return new UserResponseDTO(
                savedUser.getIdUser(),
                savedUser.getNome(),
                savedUser.getEmail(),
                savedUser.getCodigoAcesso(),
                savedUser.getAtivo(),
                savedUser.getTipoUsuario(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt(),
                mensagem
        );
    }

    /**
     * Atualizar usuário
     */
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO userRequest) {
        User existingUser = findById(id);
        
        // Log dos dados recebidos para debug
        System.out.println("=== DEBUG UPDATE USER ===");
        System.out.println("ID: " + id);
        System.out.println("Nome: " + userRequest.getNome());
        System.out.println("Email: " + userRequest.getEmail());
        System.out.println("CPF: " + userRequest.getCpf());
        System.out.println("Telefone: " + userRequest.getTelefone());
        System.out.println("CodigoAcesso: " + userRequest.getCodigoAcesso());
        System.out.println("TipoUsuario: " + userRequest.getTipoUsuario());
        System.out.println("Ativo: " + userRequest.getAtivo());
        System.out.println("========================");
        
        // Validação de email único (exceto para o próprio usuário)
        if (!existingUser.getEmail().equals(userRequest.getEmail()) && 
            userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + userRequest.getEmail());
        }
        
        // Se código foi fornecido, validar
        if (userRequest.getCodigoAcesso() != null && !userRequest.getCodigoAcesso().trim().isEmpty()) {
            String codigoNormalizado = codigoAcessoService.normalizarCodigo(userRequest.getCodigoAcesso());
            
            if (!codigoAcessoService.isCodigoValido(codigoNormalizado)) {
                throw new IllegalArgumentException("Código de acesso deve ter 6 caracteres alfanuméricos");
            }
            
            if (!existingUser.getCodigoAcesso().equalsIgnoreCase(codigoNormalizado) && 
                userRepository.existsByCodigoAcessoIgnoreCase(codigoNormalizado)) {
                throw new IllegalArgumentException("Código de acesso já cadastrado: " + codigoNormalizado);
            }
            
            existingUser.setCodigoAcesso(codigoNormalizado);
        }

        // Atualizar apenas campos não nulos
        if (userRequest.getNome() != null && !userRequest.getNome().trim().isEmpty()) {
            existingUser.setNome(userRequest.getNome());
        }
        
        if (userRequest.getEmail() != null && !userRequest.getEmail().trim().isEmpty()) {
            existingUser.setEmail(userRequest.getEmail());
        }
        
        if (userRequest.getCpf() != null && !userRequest.getCpf().trim().isEmpty()) {
            existingUser.setCpf(userRequest.getCpf());
        }
        
        if (userRequest.getTelefone() != null && !userRequest.getTelefone().trim().isEmpty()) {
            existingUser.setTelefone(userRequest.getTelefone());
        }
        
        // Só atualiza a senha se foi fornecida
        if (userRequest.getSenha() != null && !userRequest.getSenha().trim().isEmpty()) {
            existingUser.setSenha(passwordEncoder.encode(userRequest.getSenha()));
        }
        
        if (userRequest.getAtivo() != null) {
            existingUser.setAtivo(userRequest.getAtivo());
        }
        
        // Atualizar tipo de usuário se fornecido
        if (userRequest.getTipoUsuario() != null && !userRequest.getTipoUsuario().trim().isEmpty()) {
            existingUser.setTipoUsuario(userRequest.getTipoUsuario());
        }

        User savedUser = userRepository.save(existingUser);
        
        return new UserResponseDTO(
                savedUser.getIdUser(),
                savedUser.getNome(),
                savedUser.getEmail(),
                savedUser.getCodigoAcesso(),
                savedUser.getAtivo(),
                savedUser.getTipoUsuario(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt(),
                "Usuário atualizado com sucesso!"
        );
    }

    /**
     * Deletar usuário
     */
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    /**
     * Alternar status do usuário
     */
    @Transactional
    public User toggleStatus(Long id) {
        User user = findById(id);
        user.setAtivo(!user.getAtivo());
        return userRepository.save(user);
    }

    /**
     * Listar usuários ativos
     */
    public Page<User> findAllActive(Pageable pageable) {
        return userRepository.findByAtivoTrue(pageable);
    }

    /**
     * Verificar se código de acesso existe (case-insensitive)
     */
    public boolean existsByCodigoAcesso(String codigoAcesso) {
        String codigoNormalizado = codigoAcessoService.normalizarCodigo(codigoAcesso);
        return userRepository.existsByCodigoAcessoIgnoreCase(codigoNormalizado);
    }
}