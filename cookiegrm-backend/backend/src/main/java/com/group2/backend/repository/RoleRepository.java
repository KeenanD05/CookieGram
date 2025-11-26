package com.group2.backend.repository;

import com.group2.backend.model.AppRole;
import com.group2.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
     Optional<Role> findByRoleName(AppRole appRole);
}
