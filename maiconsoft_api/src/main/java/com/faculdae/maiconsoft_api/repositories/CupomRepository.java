package com.faculdae.maiconsoft_api.repositories;

import com.faculdae.maiconsoft_api.entities.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade Cupom
 */
@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {

    /**
     * Busca cupom por código
     * @param codigo Código do cupom
     * @return Optional do cupom
     */
    Optional<Cupom> findByCodigo(String codigo);

    /**
     * Busca cupons por status
     * @param status Status do cupom
     * @return Lista de cupons
     */
    List<Cupom> findByStatus(String status);
}