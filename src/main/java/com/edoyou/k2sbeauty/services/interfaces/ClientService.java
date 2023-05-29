package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Client;

import java.util.Optional;

/**
 * ClientService is an interface that specifies the business operations applicable to {@link Client}
 * entities.
 */
public interface ClientService {

  /**
   * Retrieves a Client entity based on the provided email.
   *
   * @param email The email to search for.
   * @return An Optional containing the found Client, or an empty Optional if no Client is found.
   */
  Optional<Client> findClientByEmail(String email);

  /**
   * Saves the provided Client entity to the repository.
   *
   * @param client The Client entity to save.
   */
  void saveClient(Client client);
}