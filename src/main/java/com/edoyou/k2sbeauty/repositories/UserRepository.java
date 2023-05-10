package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
  Optional<User> findByRoles_Name(@Param("roleName") String roleName);
}