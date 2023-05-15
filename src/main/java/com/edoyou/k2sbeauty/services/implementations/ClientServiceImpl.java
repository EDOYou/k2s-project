package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.repositories.ClientRepository;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <strong>ClientServiceImpl</strong> is a class that implements
 * the {@link com.edoyou.k2sbeauty.services.interfaces.ClientService} interface. It provides methods
 * to interact with Client entity in the application, such as finding clients by their appointments
 * or by the hairdresser they have appointments with.
 *
 * @see Client
 */
@Service
public class ClientServiceImpl implements ClientService {

  private static final Logger LOGGER = LogManager.getLogger(ClientServiceImpl.class.getName());
  private final ClientRepository clientRepository;

  @Autowired
  public ClientServiceImpl(ClientRepository clientRepository) {

    this.clientRepository = clientRepository;
  }

  public void saveClient(Client client) {
    LOGGER.info("Saving the client...");
    clientRepository.save(client);
  }

  @Override
  public Optional<Client> findClientByEmail(String email) {
    LOGGER.info("Find client by e-mail...");
    return Optional.ofNullable(clientRepository.findByEmail(email));
  }
}