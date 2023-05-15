package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Client;

import java.util.Optional;

public interface ClientService {

  Optional<Client> findClientByEmail(String email);

  void saveClient(Client client);
}