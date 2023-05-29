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
 * The {@code ClientServiceImpl} class provides an implementation of the {@code ClientService} interface.
 * It is responsible for the manipulation and business logic tied to the {@code Client} entities.
 *
 * <p> This service implementation provides methods for saving a client and finding a client by email.
 *
 * <p> The class uses Spring's {@code @Service} annotation to indicate it's a service provider
 * and should be automatically detected by Spring for dependency injection.
 *
 * <p> An instance of {@code ClientRepository} is autowired to facilitate operations with the database.
 *
 * <p> Example usage:
 * <pre>{@code @Autowired
 * ClientService clientService;
 *
 * public void createClient() {
 *   Client newClient = new Client();
 *   newClient.setEmail("example@example.com");
 *   clientService.saveClient(newClient);
 * }
 * }</pre>
 *
 * @since 2023-05-28
 * @author Taghiyev Kanan
 * @see Client
 * @see ClientService
 * @see ClientRepository
 */
@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger LOGGER = LogManager.getLogger(ClientServiceImpl.class.getName());
    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {

        this.clientRepository = clientRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Saves a client instance. Before saving, checks if the client is not null.
     * If it is null, it throws a NullPointerException.
     *
     * @param client The {@code ClientService} instance to save.
     * @throws NullPointerException if the service is null.
     */
    public void saveClient(Client client) {
        LOGGER.info("Saving the client...");
        if (client == null) {
            throw new NullPointerException("Client cannot be NULL");
        }
        clientRepository.save(client);
    }

    /**
     * Retrieves a client entity by its email attribute.
     *
     * <p>This method attempts to find a client in the database by the email provided.
     * The search is performed using the {@code findByEmail} method of the {@code ClientRepository}.
     * If a client with the given email exists, it is returned wrapped in an {@code Optional}.
     * If no such client exists, an empty {@code Optional} is returned.
     *
     * <p><strong>Note:</strong> The email is case-sensitive.
     *
     * <p>Example usage:
     * <pre>{@code
     * Optional<Client> clientOpt = clientService.findClientByEmail("example@example.com");
     * if (clientOpt.isPresent()) {
     *   Client client = clientOpt.get();
     *   // do something with the client
     * } else {
     *   // client not found
     * }
     * }</pre>
     *
     * @param email the email of the client to find
     * @return an {@code Optional} describing the client, if found, otherwise an empty {@code Optional}
     * @throws NullPointerException if the email is {@code null}
     */
    @Override
    public Optional<Client> findClientByEmail(String email) {
        LOGGER.info("Find client by e-mail...");
        return Optional.ofNullable(clientRepository.findByEmail(email));
    }
}