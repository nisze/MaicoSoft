package com.faculdae.maiconsoft_api.repositories;

import com.faculdae.maiconsoft_api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByCodigoAcesso(String codigoAcesso);
    boolean existsByCodigoAcesso(String codigoAcesso);
    Optional<User> findByCodigoAcessoAndAtivoTrue(String codigoAcesso);
    Page<User> findByAtivoTrue(Pageable pageable);
    
    /**
     * Conta usuários ativos
     * @return Número de usuários ativos
     */
    long countByAtivoTrue();
    
    // Métodos case-insensitive para código de acesso
    Optional<User> findByCodigoAcessoIgnoreCase(String codigoAcesso);
    boolean existsByCodigoAcessoIgnoreCase(String codigoAcesso);
    Optional<User> findByCodigoAcessoIgnoreCaseAndAtivoTrue(String codigoAcesso);
}
