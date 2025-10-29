package com.faculdae.maiconsoft_api.controllers;

import com.faculdae.maiconsoft_api.dto.user.UserRequestDTO;
import com.faculdae.maiconsoft_api.dto.LoginRequestDTO;
import com.faculdae.maiconsoft_api.dto.LoginResponseDTO;
import com.faculdae.maiconsoft_api.dto.UserResponseDTO;
import com.faculdae.maiconsoft_api.entities.User;
import com.faculdae.maiconsoft_api.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints para gestão de usuários/funcionários do sistema")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica usuário com código de acesso e senha")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest.getCodigoAcesso(), loginRequest.getSenha());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário/funcionário. Código de acesso é gerado automaticamente se não fornecido.")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequest) {
        UserResponseDTO newUser = userService.save(userRequest);
        return ResponseEntity.created(URI.create("/api/users/" + newUser.getId())).body(newUser);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários com paginação")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico pelo ID")
    public ResponseEntity<User> getUserById(@Parameter(description = "ID do usuário") @PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza um usuário existente")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequest) {
        UserResponseDTO updatedUser = userService.update(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Editar usuário (parcial)", description = "Atualiza campos específicos de um usuário existente")
    public ResponseEntity<UserResponseDTO> editUser(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @RequestBody UserRequestDTO userRequest) {
        // Para PATCH, não fazemos validação @Valid para permitir atualizações parciais
        UserResponseDTO updatedUser = userService.update(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID do usuário") @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Ativar/Desativar usuário", description = "Alterna o status ativo/inativo do usuário")
    public ResponseEntity<User> toggleUserStatus(@Parameter(description = "ID do usuário") @PathVariable Long id) {
        User user = userService.toggleStatus(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/active")
    @Operation(summary = "Listar usuários ativos", description = "Lista apenas os usuários ativos")
    public ResponseEntity<Page<User>> getActiveUsers(Pageable pageable) {
        Page<User> activeUsers = userService.findAllActive(pageable);
        return ResponseEntity.ok(activeUsers);
    }

    @GetMapping("/codigo-acesso/{codigoAcesso}")
    @Operation(summary = "Verificar código de acesso", description = "Verifica se um código de acesso já existe")
    public ResponseEntity<Boolean> checkCodigoAcesso(
            @Parameter(description = "Código de acesso a verificar") @PathVariable String codigoAcesso) {
        boolean exists = userService.existsByCodigoAcesso(codigoAcesso);
        return ResponseEntity.ok(exists);
    }
}