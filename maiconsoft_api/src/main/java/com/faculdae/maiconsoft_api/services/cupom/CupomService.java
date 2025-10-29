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
        
        updateEntityFromRequest(cupom, cupomRequest);
        Cupom savedCupom = cupomRepository.save(cupom);
        
        log.info("Cupom atualizado com sucesso - ID: {}", savedCupom.getIdCupom());
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
}