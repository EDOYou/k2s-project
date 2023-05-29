package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link UserRepository} interface extends {@link JpaRepository} for persistence operations on
 * {@link User} entities. It provides methods to retrieve User data from the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Retrieves a {@link User} entity by its email.
   *
   * @param email the email of the user to be retrieved
   * @return an {@link Optional} containing the user if found, otherwise {@link Optional#empty()}
   */
  Optional<User> findByEmail(String email);
}