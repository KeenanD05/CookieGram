package com.group2.backend.repository;

import com.group2.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    boolean existsByUserName(String user1);

    boolean existsByEmail(String mail);


    User findByEmail(String email);


    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = 'ROLE_STAFF'")
    Page<User> findStaff(Pageable pageable);
}
