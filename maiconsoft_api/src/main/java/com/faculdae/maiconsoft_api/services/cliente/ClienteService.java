package com.faculdae.maiconsoft_api.services.cliente;

import com.faculdae.maiconsoft_api.dto.cliente.ClienteRequestDTO;
import com.faculdae.maiconsoft_api.dto.cliente.ClienteResponse;
import com.faculdae.maiconsoft_api.dto.cliente.ClienteResponseDTO;
import com.faculdae.maiconsoft_api.dto.cliente.ClienteResponseDTOMapper;
import com.faculdae.maiconsoft_api.dto.viacep.ViaCepResponse;
import com.faculdae.maiconsoft_api.entities.Cliente;
import com.faculdae.maiconsoft_api.entities.User;
import com.faculdae.maiconsoft_api.repositories.ClienteRepository;
import com.faculdae.maiconsoft_api.services.external.ViaCepService;
import com.faculdae.maiconsoft_api.services.email.IEmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Serviço de negócio para gestão de clientes
 * Implementa operações CRUD com integração ViaCEP e regras de autorização
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteResponseDTOMapper clienteMapper;
    private final ViaCepService viaCepService;
    private final IEmailService emailService;

    /**
     * Salva novo cliente com integração automática do ViaCEP
     * @param clienteRequest Dados do cliente
     * @param usuarioCadastro Usuário que está cadastrando
     * @return ClienteResponseDTO com dados salvos
     */
    @Transactional
    public ClienteResponseDTO save(@Valid ClienteRequestDTO clienteRequest, User usuarioCadastro) {
        log.info("Iniciando cadastro de cliente: {}", clienteRequest.codigo());

        // Validações de negócio
        validateClienteUnique(clienteRequest.codigo(), clienteRequest.cpfCnpj());

        // Cria entidade Cliente
        Cliente cliente = buildClienteFromDTO(clienteRequest, usuarioCadastro);

        // Integração com ViaCEP se CEP fornecido
        if (clienteRequest.cep() != null && !clienteRequest.cep().trim().isEmpty()) {
            enrichClienteWithViaCep(cliente, clienteRequest.cep());
        }

        // Salva no banco
        Cliente savedCliente = clienteRepository.save(cliente);
        log.info("Cliente cadastrado com sucesso: ID={}, Codigo={}", 
                savedCliente.getIdCliente(), savedCliente.getCodigo());

        // Envia email de boas-vindas ao cliente
        enviarEmailBoasVindas(savedCliente);

        return clienteMapper.apply(savedCliente);
    }

    /**
     * Busca cliente por ID com verificação de autorização
     * @param id ID do cliente
     * @return ClienteResponseDTO
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO findById(Long id) {
        log.info("Buscando cliente por ID: {}", id);
        
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
        
        return clienteMapper.apply(cliente);
    }

    /**
     * Busca cliente por código
     * @param codigo Código único do cliente
     * @return ClienteResponseDTO
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO findByCodigo(String codigo) {
        log.info("Buscando cliente por código: {}", codigo);
        
        Cliente cliente = clienteRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com código: " + codigo));
        
        return clienteMapper.apply(cliente);
    }

    /**
     * Busca entidade Cliente por ID (para uso interno dos services)
     * @param id ID do cliente
     * @return Cliente entity
     */
    @Transactional(readOnly = true)
    public Cliente findEntityById(Long id) {
        log.info("Buscando entidade Cliente por ID: {}", id);
        
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
    }

    /**
     * Lista clientes com filtros e paginação
     * @param spec Specification para filtros
     * @param pageable Configuração de paginação
     * @return ClienteResponse com dados paginados
     */
    @Transactional(readOnly = true)
    public ClienteResponse findByFilter(Specification<Cliente> spec, Pageable pageable) {
        log.info("Buscando clientes com filtros - Página: {}, Tamanho: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Cliente> clientePage = clienteRepository.findAll(spec, pageable);
        
        log.info("Encontrados {} clientes", clientePage.getTotalElements());
        return clienteMapper.toClienteResponse(clientePage);
    }

    /**
     * Atualiza cliente existente
     * @param id ID do cliente
     * @param clienteRequest Novos dados
     * @param usuarioAtualizacao Usuário que está atualizando
     * @return ClienteResponseDTO atualizado
     */
    @Transactional
    public ClienteResponseDTO update(Long id, @Valid ClienteRequestDTO clienteRequest, User usuarioAtualizacao) {
        log.info("Atualizando cliente ID: {} por usuário: {}", id, usuarioAtualizacao.getEmail());

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        // Validações de negócio para atualização
        validateClienteUniqueForUpdate(id, clienteRequest.codigo(), clienteRequest.cpfCnpj());

        // Atualiza dados
        updateClienteFromDTO(cliente, clienteRequest);

        // Integração com ViaCEP se CEP foi alterado
        if (clienteRequest.cep() != null && !clienteRequest.cep().trim().isEmpty()) {
            enrichClienteWithViaCep(cliente, clienteRequest.cep());
        }

        Cliente updatedCliente = clienteRepository.save(cliente);
        log.info("Cliente atualizado com sucesso: ID={}", updatedCliente.getIdCliente());

        return clienteMapper.apply(updatedCliente);
    }

    /**
     * Remove cliente (apenas ADMIN/DIRETOR)
     * @param id ID do cliente
     */
    @Transactional
    public void delete(Long id) {
        log.info("Removendo cliente ID: {}", id);

        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado com ID: " + id);
        }

        clienteRepository.deleteById(id);
        log.info("Cliente removido com sucesso: ID={}", id);
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Valida se código e CPF/CNPJ são únicos
     */
    private void validateClienteUnique(String codigo, String cpfCnpj) {
        if (clienteRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Já existe cliente com código: " + codigo);
        }
        if (clienteRepository.existsByCpfCnpj(cpfCnpj)) {
            throw new IllegalArgumentException("Já existe cliente com CPF/CNPJ: " + cpfCnpj);
        }
    }

    /**
     * Valida unicidade para atualização (excluindo o próprio registro)
     */
    private void validateClienteUniqueForUpdate(Long id, String codigo, String cpfCnpj) {
        Optional<Cliente> existingByCodigo = clienteRepository.findByCodigo(codigo);
        if (existingByCodigo.isPresent() && !existingByCodigo.get().getIdCliente().equals(id)) {
            throw new IllegalArgumentException("Já existe cliente com código: " + codigo);
        }

        Optional<Cliente> existingByCpfCnpj = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (existingByCpfCnpj.isPresent() && !existingByCpfCnpj.get().getIdCliente().equals(id)) {
            throw new IllegalArgumentException("Já existe cliente com CPF/CNPJ: " + cpfCnpj);
        }
    }

    /**
     * Constrói entidade Cliente a partir do DTO
     */
    private Cliente buildClienteFromDTO(ClienteRequestDTO dto, User usuarioCadastro) {
        Cliente cliente = new Cliente();
        cliente.setCodigo(dto.codigo());
        cliente.setLoja(dto.loja());
        cliente.setRazaoSocial(dto.razaoSocial());
        cliente.setTipo(dto.tipo());
        cliente.setNomeFantasia(dto.nomeFantasia());
        cliente.setFinalidade(dto.finalidade());
        cliente.setCpfCnpj(dto.cpfCnpj());
        cliente.setCep(dto.cep());
        cliente.setPais(dto.pais());
        cliente.setEstado(dto.estado());
        cliente.setCodMunicipio(dto.codMunicipio());
        cliente.setCidade(dto.cidade());
        cliente.setEndereco(dto.endereco());
        cliente.setBairro(dto.bairro());
        cliente.setDdd(dto.ddd());
        cliente.setTelefone(dto.telefone());
        cliente.setAbertura(dto.abertura());
        cliente.setContato(dto.contato());
        cliente.setEmail(dto.email());
        cliente.setHomepage(dto.homepage());
        cliente.setDescricao(dto.descricao());
        cliente.setDatahoraCadastro(LocalDateTime.now());
        cliente.setUsuarioCadastro(usuarioCadastro);
        return cliente;
    }

    /**
     * Atualiza entidade Cliente com dados do DTO
     */
    private void updateClienteFromDTO(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setCodigo(dto.codigo());
        cliente.setLoja(dto.loja());
        cliente.setRazaoSocial(dto.razaoSocial());
        cliente.setTipo(dto.tipo());
        cliente.setNomeFantasia(dto.nomeFantasia());
        cliente.setFinalidade(dto.finalidade());
        cliente.setCpfCnpj(dto.cpfCnpj());
        cliente.setCep(dto.cep());
        cliente.setPais(dto.pais());
        cliente.setEstado(dto.estado());
        cliente.setCodMunicipio(dto.codMunicipio());
        cliente.setCidade(dto.cidade());
        cliente.setEndereco(dto.endereco());
        cliente.setBairro(dto.bairro());
        cliente.setDdd(dto.ddd());
        cliente.setTelefone(dto.telefone());
        cliente.setAbertura(dto.abertura());
        cliente.setContato(dto.contato());
        cliente.setEmail(dto.email());
        cliente.setHomepage(dto.homepage());
        cliente.setDescricao(dto.descricao());
    }

    /**
     * Enriquece dados do cliente com informações do ViaCEP
     */
    private void enrichClienteWithViaCep(Cliente cliente, String cep) {
        try {
            Optional<ViaCepResponse> viaCepResponse = viaCepService.buscarCep(cep);
            
            if (viaCepResponse.isPresent()) {
                ViaCepResponse viaCep = viaCepResponse.get();
                
                // Atualiza apenas campos vazios ou complementa informações
                if (cliente.getEndereco() == null || cliente.getEndereco().trim().isEmpty()) {
                    cliente.setEndereco(viaCep.getLogradouro());
                }
                if (cliente.getBairro() == null || cliente.getBairro().trim().isEmpty()) {
                    cliente.setBairro(viaCep.getBairro());
                }
                if (cliente.getCidade() == null || cliente.getCidade().trim().isEmpty()) {
                    cliente.setCidade(viaCep.getLocalidade());
                }
                if (cliente.getEstado() == null || cliente.getEstado().trim().isEmpty()) {
                    cliente.setEstado(viaCep.getUf());
                }
                if (cliente.getDdd() == null || cliente.getDdd().trim().isEmpty()) {
                    cliente.setDdd(viaCep.getDdd());
                }
                if (cliente.getCodMunicipio() == null || cliente.getCodMunicipio().trim().isEmpty()) {
                    cliente.setCodMunicipio(viaCep.getIbge());
                }
                
                log.info("Dados do ViaCEP integrados para CEP: {}", cep);
            } else {
                log.warn("CEP não encontrado no ViaCEP: {}", cep);
            }
        } catch (Exception e) {
            log.warn("Erro ao integrar com ViaCEP para CEP {}: {}", cep, e.getMessage());
            // Continua o cadastro mesmo com erro na integração
        }
    }

    /**
     * Envia email de boas-vindas para o cliente cadastrado
     */
    private void enviarEmailBoasVindas(Cliente cliente) {
        try {
            if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
                emailService.enviarBoasVindasCliente(
                    cliente.getEmail(), 
                    cliente.getRazaoSocial(), 
                    cliente.getCodigo()
                );
                log.info("Email de boas-vindas enviado para cliente: {} - {}", 
                        cliente.getCodigo(), cliente.getEmail());
            } else {
                log.warn("Cliente {} cadastrado sem email - não será enviado email de boas-vindas", 
                        cliente.getCodigo());
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email de boas-vindas para cliente {}: {}", 
                    cliente.getCodigo(), e.getMessage());
            // Não falha o cadastro por erro no envio de email
        }
    }
}