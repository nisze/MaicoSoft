package com.faculdae.maiconsoft_api.services.venda;

import com.faculdae.maiconsoft_api.dto.venda.VendaRequestDTO;
import com.faculdae.maiconsoft_api.dto.venda.VendaRequestFilterDTO;
import com.faculdae.maiconsoft_api.dto.venda.VendaResponse;
import com.faculdae.maiconsoft_api.dto.venda.VendaResponseDTO;
import com.faculdae.maiconsoft_api.dto.venda.VendaResponseDTOMapper;
import com.faculdae.maiconsoft_api.entities.*;
import com.faculdae.maiconsoft_api.repositories.UserRepository;
import com.faculdae.maiconsoft_api.repositories.VendaRepository;
import com.faculdae.maiconsoft_api.services.email.IEmailService;
import com.faculdae.maiconsoft_api.services.cliente.ClienteService;
import com.faculdae.maiconsoft_api.services.cupom.CupomService;
import com.faculdae.maiconsoft_api.specification.VendaSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service para gerenciamento de vendas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaResponseDTOMapper vendaMapper;
    private final ClienteService clienteService;
    private final CupomService cupomService;
    private final UserRepository userRepository;
    private final IEmailService emailService;

    /**
     * Salva uma nova venda com regras de negócio
     * @param requestDTO Dados da venda
     * @return DTO da venda criada
     */
    @Transactional
    public VendaResponseDTO save(VendaRequestDTO requestDTO) {
        log.info("Iniciando criação de venda para cliente: {}", requestDTO.clienteId());
        
        // 1. Validar e buscar cliente
        Cliente cliente = clienteService.findEntityById(requestDTO.clienteId());
        
        // 2. Buscar usuário admin padrão (sem autenticação)
        User usuarioLogado = userRepository.findByEmail("admin@maiconsoft.com")
                .orElseThrow(() -> new RuntimeException("Usuário admin não encontrado"));
        
        // 3. Processar cupom se informado
        Cupom cupom = null;
        if (requestDTO.cupomCodigo() != null && !requestDTO.cupomCodigo().trim().isEmpty()) {
            cupom = cupomService.findByCodigo(requestDTO.cupomCodigo());
            log.info("Cupom aplicado: {} - {}%", cupom.getCodigo(), cupom.getDescontoPercentual());
        }
        
        // 4. Calcular valores
        BigDecimal valorBruto = requestDTO.valorBruto();
        BigDecimal valorDesconto = calcularDesconto(valorBruto, cupom);
        BigDecimal valorTotal = valorBruto.subtract(valorDesconto);
        
        // 5. Gerar número do orçamento
        String numeroOrcamento = gerarNumeroOrcamento();
        
        // 6. Criar entidade
        Venda venda = Venda.builder()
                .numeroOrcamento(numeroOrcamento)
                .status(requestDTO.status())
                .valorBruto(valorBruto)
                .valorDesconto(valorDesconto)
                .valorTotal(valorTotal)
                .dataVenda(requestDTO.dataVenda())
                .datahoraCadastro(LocalDateTime.now())
                .observacao(requestDTO.observacao())
                .cliente(cliente)
                .cupom(cupom)
                .usuarioCadastro(usuarioLogado)
                .build();
        
        // 7. Salvar no banco
        Venda vendaSalva = vendaRepository.save(venda);
        log.info("Venda criada com sucesso - ID: {}, Orçamento: {}", 
                vendaSalva.getIdVenda(), vendaSalva.getNumeroOrcamento());
        
        // 8. Enviar email de notificação (assíncrono)
        try {
            emailService.enviarNotificacaoNovaVenda(
                venda.getUsuarioCadastro().getNome(),
                cliente.getNomeFantasia(), 
                vendaSalva.getValorTotal().doubleValue()
            );
            log.info("Email de notificação de venda enviado para diretor");
        } catch (Exception e) {
            log.error("Erro ao enviar email de notificação da venda: {}", e.getMessage());
            // Não falha a venda por causa do email
        }
        
        return vendaMapper.apply(vendaSalva);
    }

    /**
     * Busca vendas com filtros e paginação (método simplificado)
     * @param clienteNome Nome do cliente (opcional)
     * @param status Status da venda (opcional)
     * @param dataInicio Data inicial (opcional)
     * @param dataFim Data final (opcional)
     * @param page Página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Página de vendas
     */
    public VendaResponse findByFilter(String clienteNome, String status, 
                                     LocalDate dataInicio, LocalDate dataFim,
                                     int page, int size, String sortBy, String sortDir) {
        
        VendaRequestFilterDTO filter = VendaRequestFilterDTO.builder()
                .clienteNome(clienteNome)
                .status(status)
                .dataVendaInicio(dataInicio)
                .dataVendaFim(dataFim)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
                
        return findByAdvancedFilter(filter);
    }

    /**
     * Busca vendas com filtros avançados
     * @param filter DTO com filtros avançados
     * @return Página de vendas
     */
    public VendaResponse findByAdvancedFilter(VendaRequestFilterDTO filter) {
        log.info("Buscando vendas com filtros avançados - Cliente: {}, Status: {}, Período: {} a {}", 
                filter.getClienteNome(), filter.getStatus(), 
                filter.getDataVendaInicio(), filter.getDataVendaFim());
        
        // Configurar ordenação
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDir()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        
        // Construir specification com todos os filtros
        Specification<Venda> spec = buildSpecification(filter);
        
        // Buscar com filtros
        Page<Venda> vendaPage = vendaRepository.findAll(spec, pageable);
        
        log.info("Encontradas {} vendas com os filtros aplicados", vendaPage.getTotalElements());
        
        return vendaMapper.toVendaResponse(vendaPage);
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Constrói Specification baseada nos filtros
     */
    private Specification<Venda> buildSpecification(VendaRequestFilterDTO filter) {
        Specification<Venda> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        
        // Filtros de cliente
        if (filter.getClienteNome() != null && !filter.getClienteNome().trim().isEmpty()) {
            spec = spec.and(VendaSpecification.hasClienteNome(filter.getClienteNome()));
        }
        
        if (filter.getClienteCodigo() != null && !filter.getClienteCodigo().trim().isEmpty()) {
            spec = spec.and(VendaSpecification.hasClienteCodigo(filter.getClienteCodigo()));
        }
        
        // Filtros de status
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            spec = spec.and(VendaSpecification.hasStatus(filter.getStatus()));
        }
        
        if (filter.getStatusList() != null && !filter.getStatusList().isEmpty()) {
            spec = spec.and(VendaSpecification.hasStatusIn(filter.getStatusList()));
        }
        
        // Filtros de número do orçamento
        if (filter.getNumeroOrcamento() != null && !filter.getNumeroOrcamento().trim().isEmpty()) {
            spec = spec.and(VendaSpecification.hasNumeroOrcamento(filter.getNumeroOrcamento()));
        }
        
        // Filtros de valor
        if (filter.getValorMinimo() != null || filter.getValorMaximo() != null) {
            spec = spec.and(VendaSpecification.hasValorTotalBetween(
                filter.getValorMinimo(), filter.getValorMaximo()));
        }
        
        if (filter.getDescontoMinimo() != null) {
            spec = spec.and(VendaSpecification.hasDescontoGreaterThan(filter.getDescontoMinimo()));
        }
        
        // Filtros de data de venda
        if (filter.getDataVendaInicio() != null || filter.getDataVendaFim() != null) {
            spec = spec.and(VendaSpecification.hasDataVendaBetween(
                filter.getDataVendaInicio(), filter.getDataVendaFim()));
        }
        
        // Filtros de data de cadastro
        if (filter.getDataCadastroInicio() != null || filter.getDataCadastroFim() != null) {
            spec = spec.and(VendaSpecification.hasDataCadastroBetween(
                filter.getDataCadastroInicio(), filter.getDataCadastroFim()));
        }
        
        // Filtros de usuário
        if (filter.getUsuarioId() != null) {
            spec = spec.and(VendaSpecification.hasUsuarioCadastro(filter.getUsuarioId()));
        }
        
        if (filter.getUsuarioCodigo() != null && !filter.getUsuarioCodigo().trim().isEmpty()) {
            spec = spec.and(VendaSpecification.hasUsuarioCadastroCodigo(filter.getUsuarioCodigo()));
        }
        
        // Filtros de cupom
        if (filter.getCupomCodigo() != null && !filter.getCupomCodigo().trim().isEmpty()) {
            spec = spec.and(VendaSpecification.hasCupomCodigo(filter.getCupomCodigo()));
        }
        
        if (filter.getComCupom() != null) {
            if (filter.getComCupom()) {
                spec = spec.and(VendaSpecification.hasCupom());
            } else {
                spec = spec.and(VendaSpecification.hasNoCupom());
            }
        }
        
        return spec;
    }

    /**
     * Busca venda por ID
     * @param id ID da venda
     * @return DTO da venda
     */
    public VendaResponseDTO findById(Long id) {
        return vendaRepository.findById(id)
                .map(vendaMapper)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));
    }

    /**
     * Busca venda por número do orçamento
     * @param numeroOrcamento Número do orçamento
     * @return DTO da venda
     */
    public VendaResponseDTO findByNumeroOrcamento(String numeroOrcamento) {
        return vendaRepository.findByNumeroOrcamento(numeroOrcamento)
                .map(vendaMapper)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com orçamento: " + numeroOrcamento));
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Calcula o valor do desconto baseado no cupom
     */
    private BigDecimal calcularDesconto(BigDecimal valorBruto, Cupom cupom) {
        if (cupom == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal percentualDesconto = BigDecimal.valueOf(cupom.getDescontoPercentual());
        return valorBruto
                .multiply(percentualDesconto)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Gera número sequencial de orçamento no formato: AAAAMM9999
     * Ex: 202412001, 202412002, etc.
     */
    private String gerarNumeroOrcamento() {
        String prefixo = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        Optional<String> ultimoNumero = vendaRepository.findUltimoNumeroOrcamentoPorMes(prefixo);
        
        if (ultimoNumero.isPresent()) {
            // Extrair o sequencial e incrementar
            String numeroCompleto = ultimoNumero.get();
            int sequencial = Integer.parseInt(numeroCompleto.substring(6)) + 1;
            return prefixo + String.format("%03d", sequencial);
        } else {
            // Primeiro orçamento do mês
            return prefixo + "001";
        }
    }

    /**
     * Atualiza uma venda existente
     * @param id ID da venda
     * @param vendaRequest Dados para atualização
     * @return Venda atualizada
     */
    public VendaResponseDTO update(Long id, VendaRequestDTO vendaRequest) {
        log.info("Atualizando venda ID: {}", id);
        
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));
        
        // Buscar cliente
        Cliente cliente = clienteService.findEntityById(vendaRequest.clienteId());
        venda.setCliente(cliente);
        
        // Processar cupom se fornecido
        Cupom cupom = null;
        if (vendaRequest.cupomCodigo() != null && !vendaRequest.cupomCodigo().trim().isEmpty()) {
            cupom = cupomService.findByCodigo(vendaRequest.cupomCodigo().trim());
            log.info("Cupom aplicado: {} - {}%", cupom.getCodigo(), cupom.getDescontoPercentual());
        }
        venda.setCupom(cupom);
        
        // Atualizar campos
        venda.setStatus(vendaRequest.status());
        venda.setValorBruto(vendaRequest.valorBruto());
        venda.setDataVenda(vendaRequest.dataVenda());
        venda.setObservacao(vendaRequest.observacao());
        
        // Recalcular valores
        BigDecimal valorDesconto = BigDecimal.ZERO;
        if (cupom != null) {
            valorDesconto = calcularDesconto(vendaRequest.valorBruto(), cupom);
        }
        venda.setValorDesconto(valorDesconto);
        venda.setValorTotal(vendaRequest.valorBruto().subtract(valorDesconto));
        
        Venda vendaSalva = vendaRepository.save(venda);
        log.info("Venda atualizada com sucesso - ID: {}", vendaSalva.getIdVenda());
        
        return vendaMapper.apply(vendaSalva);
    }

    /**
     * Exclui uma venda
     * @param id ID da venda
     */
    public void delete(Long id) {
        log.info("Excluindo venda ID: {}", id);
        
        if (!vendaRepository.existsById(id)) {
            throw new RuntimeException("Venda não encontrada com ID: " + id);
        }
        
        vendaRepository.deleteById(id);
        log.info("Venda excluída com sucesso - ID: {}", id);
    }
}