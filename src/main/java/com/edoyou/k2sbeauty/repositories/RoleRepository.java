package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link RoleRepository} interface extends {@link JpaRepository} for database operations on
 * {@link Role} entities. It provides methods to retrieve Role data from the database.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  /**
   * Retrieves a {@link Role} entity by its name.
   *
   * @param name the name of the Role
   * @return an {@link Optional} containing the Role entity if found, otherwise
   * {@link Optional#empty()}
   */
  Optional<Role> findByName(String name);
}
