package com.faculdae.maiconsoft_api.controllers;

import com.faculdae.maiconsoft_api.dto.user.UserRequestDTO;
import com.faculdae.maiconsoft_api.dto.user.LoginRequestDTO;
import com.faculdae.maiconsoft_api.dto.user.LoginResponseDTO;
import com.faculdae.maiconsoft_api.dto.user.UserResponseDTO;
import com.faculdae.maiconsoft_api.entities.UserRole;
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
        return ResponseEntity.created(URI.create("/api/users/" + newUser.getIdUser())).body(newUser);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários com paginação")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userService.findAllAsDTO(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico pelo ID")
    public ResponseEntity<UserResponseDTO> getUserById(@Parameter(description = "ID do usuário") @PathVariable Long id) {
        UserResponseDTO user = userService.findByIdAsDTO(id);
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
    public ResponseEntity<UserResponseDTO> toggleUserStatus(@Parameter(description = "ID do usuário") @PathVariable Long id) {
        UserResponseDTO user = userService.toggleStatusAsDTO(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/active")
    @Operation(summary = "Listar usuários ativos", description = "Lista apenas os usuários ativos")
    public ResponseEntity<Page<UserResponseDTO>> getActiveUsers(Pageable pageable) {
        Page<UserResponseDTO> activeUsers = userService.findAllActiveAsDTO(pageable);
        return ResponseEntity.ok(activeUsers);
    }

    @GetMapping("/codigo-acesso/{codigoAcesso}")
    @Operation(summary = "Verificar código de acesso", description = "Verifica se um código de acesso já existe")
    public ResponseEntity<Boolean> checkCodigoAcesso(
            @Parameter(description = "Código de acesso a verificar") @PathVariable String codigoAcesso) {
        boolean exists = userService.existsByCodigoAcesso(codigoAcesso);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles disponíveis", description = "Lista todas as roles/perfis disponíveis no sistema")
    public ResponseEntity<java.util.List<UserRole>> getAllRoles() {
        java.util.List<UserRole> roles = userService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/{roleName}")
    @Operation(summary = "Buscar role por nome", description = "Busca uma role específica pelo nome")
    public ResponseEntity<UserRole> getRoleByName(
            @Parameter(description = "Nome da role") @PathVariable String roleName) {
        UserRole role = userService.findRoleByName(roleName);
        return ResponseEntity.ok(role);
    }
}