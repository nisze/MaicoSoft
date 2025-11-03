package com.faculdae.maiconsoft_api.services.cupom;

import com.faculdae.maiconsoft_api.dto.cupom.CupomRequestDTO;
import com.faculdae.maiconsoft_api.dto.cupom.CupomResponseDTO;
import com.faculdae.maiconsoft_api.entities.Cupom;
import com.faculdae.maiconsoft_api.repositories.CupomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de cupons
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CupomService {

    private final CupomRepository cupomRepository;

    /**
     * Busca cupom por código
     * @param codigo Código do cupom
     * @return Cupom encontrado
     * @throws RuntimeException se não encontrado
     */
    public Cupom findByCodigo(String codigo) {
        log.info("Buscando cupom por código: {}", codigo);
        
        return cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Cupom não encontrado: " + codigo));
    }

    /**
     * Lista todos os cupons com paginação
     */
    public Page<CupomResponseDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os cupons - Página: {}", pageable.getPageNumber());
        
        // Desativar cupons expirados antes de retornar
        desativarCuponsExpirados();
        
        return cupomRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Busca cupom por ID
     */
    public CupomResponseDTO findById(Long id) {
        log.info("Buscando cupom por ID: {}", id);
        
        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupom não encontrado com ID: " + id));
        
        return convertToResponseDTO(cupom);
    }

    /**
     * Busca cupons por status
     */
    public List<CupomResponseDTO> findByStatus(String status) {
        log.info("Buscando cupons por status: {}", status);
        
        return cupomRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Salva novo cupom
     */
    public CupomResponseDTO save(CupomRequestDTO cupomRequest) {
        log.info("Criando novo cupom: {}", cupomRequest.getCodigo());
        
        // Verifica se código já existe
        if (cupomRepository.findByCodigo(cupomRequest.getCodigo()).isPresent()) {
            throw new RuntimeException("Já existe um cupom com o código: " + cupomRequest.getCodigo());
        }
        
        // Validação de data para cupons ativos
        validateCupomData(cupomRequest);
        
        Cupom cupom = convertToEntity(cupomRequest);
        Cupom savedCupom = cupomRepository.save(cupom);
        
        log.info("Cupom criado com sucesso - ID: {}", savedCupom.getIdCupom());
        return convertToResponseDTO(savedCupom);
    }

    /**
     * Atualiza cupom existente
     */
    public CupomResponseDTO update(Long id, CupomRequestDTO cupomRequest) {
        log.info("Atualizando cupom ID: {}", id);
        
        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupom não encontrado com ID: " + id));
        
        // Verifica se código já existe em outro cupom
        cupomRepository.findByCodigo(cupomRequest.getCodigo())
                .ifPresent(existingCupom -> {
                    if (!existingCupom.getIdCupom().equals(id)) {
                        throw new RuntimeException("Já existe um cupom com o código: " + cupomRequest.getCodigo());
                    }
                });
        
        // Validação de data APENAS se estiver tentando ativar um cupom
        // Permite desativar cupons expirados sem erro
        if ("ATIVO".equals(cupomRequest.getStatus())) {
            validateCupomData(cupomRequest);
        }
        
        updateEntityFromRequest(cupom, cupomRequest);
        Cupom savedCupom = cupomRepository.save(cupom);
        
        log.info("Cupom atualizado com sucesso - ID: {}", savedCupom.getIdCupom());
        return convertToResponseDTO(savedCupom);
    }

    /**
     * Alterna o status do cupom entre ATIVO e INATIVO
     */
    public CupomResponseDTO toggleStatus(Long id) {
        log.info("Alternando status do cupom ID: {}", id);
        
        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupom não encontrado com ID: " + id));
        
        // Alternar status
        String novoStatus = "ATIVO".equals(cupom.getStatus()) ? "INATIVO" : "ATIVO";
        cupom.setStatus(novoStatus);
        
        // Se está ativando, verificar se a data é válida
        if ("ATIVO".equals(novoStatus)) {
            if (cupom.getValidade() != null && cupom.getValidade().isBefore(LocalDate.now())) {
                throw new RuntimeException("Não é possível ativar cupom com data de validade expirada");
            }
        }
        
        Cupom savedCupom = cupomRepository.save(cupom);
        
        String action = "ATIVO".equals(novoStatus) ? "ativado" : "desativado";
        log.info("Cupom {} com sucesso - ID: {}", action, savedCupom.getIdCupom());
        
        return convertToResponseDTO(savedCupom);
    }

    /**
     * Exclui cupom
     */
    public void delete(Long id) {
        log.info("Excluindo cupom ID: {}", id);
        
        if (!cupomRepository.existsById(id)) {
            throw new RuntimeException("Cupom não encontrado com ID: " + id);
        }
        
        cupomRepository.deleteById(id);
        log.info("Cupom excluído com sucesso - ID: {}", id);
    }

    /**
     * Converte entidade para DTO de resposta
     */
    private CupomResponseDTO convertToResponseDTO(Cupom cupom) {
        CupomResponseDTO dto = new CupomResponseDTO();
        dto.setIdCupom(cupom.getIdCupom());
        dto.setCodigo(cupom.getCodigo());
        dto.setNome(cupom.getNome());
        dto.setDescricao(cupom.getDescricao());
        dto.setDescontoPercentual(cupom.getDescontoPercentual());
        dto.setDescontoValor(cupom.getDescontoValor());
        dto.setValidade(cupom.getValidade());
        dto.setStatus(cupom.getStatus());
        dto.setValorMinimo(cupom.getValorMinimo());
        dto.setMaxUsos(cupom.getMaxUsos());
        dto.setUsosAtuais(cupom.getUsosAtual()); // Corrigido para usar usosAtual da entidade
        
        // Flags calculadas
        dto.setAtivo("ATIVO".equals(cupom.getStatus()));
        dto.setExpirado(cupom.getValidade() != null && cupom.getValidade().isBefore(LocalDate.now()));
        dto.setLimiteEsgotado(cupom.getMaxUsos() != null && (cupom.getUsosAtual() != null ? cupom.getUsosAtual() : 0) >= cupom.getMaxUsos());
        
        return dto;
    }

    /**
     * Converte DTO de request para entidade
     */
    private Cupom convertToEntity(CupomRequestDTO dto) {
        Cupom cupom = new Cupom();
        updateEntityFromRequest(cupom, dto);
        return cupom;
    }

    /**
     * Atualiza entidade com dados do request
     */
    private void updateEntityFromRequest(Cupom cupom, CupomRequestDTO dto) {
        cupom.setCodigo(dto.getCodigo());
        cupom.setNome(dto.getNome());
        cupom.setDescricao(dto.getDescricao());
        cupom.setDescontoPercentual(dto.getDescontoPercentual());
        cupom.setDescontoValor(dto.getDescontoValor());
        cupom.setValidade(dto.getValidade());
        cupom.setStatus(dto.getStatus());
        cupom.setValorMinimo(dto.getValorMinimo());
        cupom.setMaxUsos(dto.getMaxUsos());
        // Manter o valor atual de usosAtual quando atualizando cupom
        if (dto.getUsosAtuais() != null) {
            cupom.setUsosAtual(dto.getUsosAtuais());
        }
    }

    /**
     * Valida dados do cupom baseado no status
     */
    private void validateCupomData(CupomRequestDTO cupomRequest) {
        // Só validar data futura se o cupom estiver ATIVO
        if ("ATIVO".equals(cupomRequest.getStatus())) {
            if (cupomRequest.getValidade() != null && cupomRequest.getValidade().isBefore(LocalDate.now())) {
                throw new RuntimeException("Cupons ativos devem ter data de validade no futuro");
            }
        }
    }

    /**
     * Incrementa o uso do cupom
     * @param cupomCodigo Código do cupom
     * @throws RuntimeException se cupom não puder ser usado
     */
    public void incrementarUsoCupom(String cupomCodigo) {
        log.info("Incrementando uso do cupom: {}", cupomCodigo);
        
        Cupom cupom = findByCodigo(cupomCodigo);
        
        // Verificar se cupom pode ser usado
        if (!"ATIVO".equals(cupom.getStatus())) {
            throw new RuntimeException("Cupom não está ativo: " + cupomCodigo);
        }
        
        if (cupom.getValidade() != null && cupom.getValidade().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cupom expirado: " + cupomCodigo);
        }
        
        int usosAtuais = cupom.getUsosAtual() != null ? cupom.getUsosAtual() : 0;
        if (cupom.getMaxUsos() != null && usosAtuais >= cupom.getMaxUsos()) {
            throw new RuntimeException("Cupom atingiu o limite máximo de usos: " + cupomCodigo);
        }
        
        // Incrementar uso
        cupom.setUsosAtual(usosAtuais + 1);
        cupomRepository.save(cupom);
        
        log.info("Uso incrementado para cupom {} - Usos atuais: {}", cupomCodigo, cupom.getUsosAtual());
    }

    /**
     * Verifica se um cupom pode ser usado (sem incrementar o uso)
     * @param cupomCodigo Código do cupom
     * @return true se o cupom pode ser usado
     */
    public boolean podeUsarCupom(String cupomCodigo) {
        try {
            Cupom cupom = findByCodigo(cupomCodigo);
            
            // Verificar se cupom está ativo
            if (!"ATIVO".equals(cupom.getStatus())) {
                return false;
            }
            
            // Verificar se não expirou
            if (cupom.getValidade() != null && cupom.getValidade().isBefore(LocalDate.now())) {
                return false;
            }
            
            // Verificar limite de usos
            int usosAtuais = cupom.getUsosAtual() != null ? cupom.getUsosAtual() : 0;
            if (cupom.getMaxUsos() != null && usosAtuais >= cupom.getMaxUsos()) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Desativa automaticamente cupons que expiraram
     */
    private void desativarCuponsExpirados() {
        log.info("Verificando cupons expirados para desativação automática");
        
        LocalDate hoje = LocalDate.now();
        List<Cupom> cuponsExpirados = cupomRepository.findByStatusAndValidadeBefore("ATIVO", hoje);
        
        if (!cuponsExpirados.isEmpty()) {
            cuponsExpirados.forEach(cupom -> {
                log.info("Desativando cupom expirado: {} (venceu em {})", cupom.getCodigo(), cupom.getValidade());
                cupom.setStatus("INATIVO");
            });
            
            cupomRepository.saveAll(cuponsExpirados);
            log.info("Desativados {} cupons expirados", cuponsExpirados.size());
        }
    }
}