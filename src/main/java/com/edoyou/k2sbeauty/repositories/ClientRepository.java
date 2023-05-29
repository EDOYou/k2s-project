package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link ClientRepository} interface extends {@link JpaRepository} for persistence operations on
 * {@link Client} entities. It provides methods to retrieve Client data from the database.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  /**
   * Retrieves a {@link Client} entity by its email.
   *
   * @param email the email of the client to be retrieved
   * @return the Client entity if found, otherwise null
   */
  Client findByEmail(String email);
}