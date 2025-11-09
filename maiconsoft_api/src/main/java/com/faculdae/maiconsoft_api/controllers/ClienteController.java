package com.faculdae.maiconsoft_api.controllers;

import com.faculdae.maiconsoft_api.dto.cliente.ClienteRequestDTO;
import com.faculdae.maiconsoft_api.dto.cliente.ClienteResponse;
import com.faculdae.maiconsoft_api.dto.cliente.ClienteResponseDTO;
import com.faculdae.maiconsoft_api.entities.User;
import com.faculdae.maiconsoft_api.repositories.UserRepository;
import com.faculdae.maiconsoft_api.services.cliente.ClienteService;
import com.faculdae.maiconsoft_api.specification.ClienteSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller REST para operações de Cliente
 * Endpoints sem autenticação para teste de CRUD
 */
@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gestão de clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final UserRepository userRepository;

    @Operation(summary = "Cadastrar novo cliente", 
               description = "Cria um novo cliente com integração automática ViaCEP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Cliente já existe (código ou CPF/CNPJ duplicado)")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> create(
            @Valid @RequestBody ClienteRequestDTO clienteRequest) {

        User usuarioCadastro = userRepository.findByEmail("admin@maiconsoft.com")
                .orElseThrow(() -> new RuntimeException("Usuário admin não encontrado"));
        
        ClienteResponseDTO response = clienteService.save(clienteRequest, usuarioCadastro);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar cliente por ID", 
               description = "Retorna dados completos do cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    
    public ResponseEntity<ClienteResponseDTO> findById(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        
        ClienteResponseDTO response = clienteService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar cliente por código", 
               description = "Retorna dados completos do cliente pelo código único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/codigo/{codigo}")
    
    public ResponseEntity<ClienteResponseDTO> findByCodigo(
            @Parameter(description = "Código único do cliente") @PathVariable String codigo) {
        
        ClienteResponseDTO response = clienteService.findByCodigo(codigo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar clientes com filtros", 
               description = "Lista clientes com filtros opcionais e paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping
    
    public ResponseEntity<ClienteResponse> findAll(
            @Parameter(description = "Código do cliente") @RequestParam(required = false) String codigo,
            @Parameter(description = "Razão social (busca parcial)") @RequestParam(required = false) String razaoSocial,
            @Parameter(description = "Nome fantasia (busca parcial)") @RequestParam(required = false) String nomeFantasia,
            @Parameter(description = "Tipo do cliente (F=Física, J=Jurídica)") @RequestParam(required = false) String tipo,
            @Parameter(description = "CPF/CNPJ") @RequestParam(required = false) String cpfCnpj,
            @Parameter(description = "Cidade (busca parcial)") @RequestParam(required = false) String cidade,
            @Parameter(description = "Estado (UF)") @RequestParam(required = false) String estado,
            @Parameter(description = "Email (busca parcial)") @RequestParam(required = false) String email,
            @Parameter(description = "Data inicial de cadastro") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicialCadastro,
            @Parameter(description = "Data final de cadastro") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinalCadastro,
            @Parameter(description = "Página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "datahoraCadastro") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<com.faculdae.maiconsoft_api.entities.Cliente> spec = 
            ClienteSpecification.build(codigo, razaoSocial, nomeFantasia, tipo, cpfCnpj, 
                                     cidade, estado, email, dataInicialCadastro, dataFinalCadastro);

        ClienteResponse response = clienteService.findByFilter(spec, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca livre por texto", 
               description = "Busca clientes por texto livre em múltiplos campos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping("/search")
    
    public ResponseEntity<ClienteResponse> searchByText(
            @Parameter(description = "Texto para busca livre") @RequestParam String searchText,
            @Parameter(description = "Página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "razaoSocial") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<com.faculdae.maiconsoft_api.entities.Cliente> spec = 
            ClienteSpecification.searchByText(searchText);

        ClienteResponse response = clienteService.findByFilter(spec, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar cliente", 
               description = "Atualiza dados do cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito de dados (código ou CPF/CNPJ duplicado)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> update(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO clienteRequest) {

        User usuarioAtualizacao = userRepository.findByEmail("admin@maiconsoft.com")
                .orElseThrow(() -> new RuntimeException("Usuário admin não encontrado"));
        
        ClienteResponseDTO response = clienteService.update(id, clienteRequest, usuarioAtualizacao);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remover cliente", 
               description = "Remove cliente do sistema (apenas ADMIN/DIRETOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (apenas ADMIN/DIRETOR)"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {

        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
