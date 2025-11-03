package com.faculdae.maiconsoft_api.controllers;

import com.faculdae.maiconsoft_api.dto.cupom.CupomRequestDTO;
import com.faculdae.maiconsoft_api.dto.cupom.CupomResponseDTO;
import com.faculdae.maiconsoft_api.entities.Cupom;
import com.faculdae.maiconsoft_api.services.cupom.CupomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
public class CupomController {

    private final CupomService cupomService;

    @GetMapping
    public ResponseEntity<Page<CupomResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "idCupom") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Listando cupons - Página: {}", page);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CupomResponseDTO> cupons = cupomService.findAll(pageable);
        
        return ResponseEntity.ok(cupons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CupomResponseDTO> findById(@PathVariable Long id) {
        CupomResponseDTO cupom = cupomService.findById(id);
        return ResponseEntity.ok(cupom);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<CupomResponseDTO>> findAtivos() {
        List<CupomResponseDTO> cupons = cupomService.findByStatus("ATIVO");
        return ResponseEntity.ok(cupons);
    }

    @PostMapping
    public ResponseEntity<CupomResponseDTO> create(@Valid @RequestBody CupomRequestDTO cupomRequest) {
        log.info("Criando novo cupom: {}", cupomRequest.getCodigo());
        CupomResponseDTO cupom = cupomService.save(cupomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(cupom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CupomResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CupomRequestDTO cupomRequest) {
        log.info("Atualizando cupom ID: {}", id);
        CupomResponseDTO cupom = cupomService.update(id, cupomRequest);
        return ResponseEntity.ok(cupom);
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<CupomResponseDTO> toggleStatus(@PathVariable Long id) {
        log.info("Alternando status do cupom ID: {}", id);
        CupomResponseDTO cupom = cupomService.toggleStatus(id);
        return ResponseEntity.ok(cupom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Excluindo cupom ID: {}", id);
        cupomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}