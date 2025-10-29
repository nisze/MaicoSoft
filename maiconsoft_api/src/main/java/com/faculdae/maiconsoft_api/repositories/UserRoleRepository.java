package com.faculdae.maiconsoft_api.repositories;

import com.faculdae.maiconsoft_api.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findByRoleNameIgnoreCase(String roleName);

    UserRole findByRoleName(String roleName);
}

