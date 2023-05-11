package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.ClientRepository;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.security.CustomUserDetailsService;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

  private final CustomUserDetailsService customUserDetailsService;
  private final ClientRepository clientRepository;

  @Autowired
  public ClientServiceImpl(ClientRepository clientRepository, CustomUserDetailsService customUserDetailsService) {
    this.clientRepository = clientRepository;
    this.customUserDetailsService = customUserDetailsService;
  }

  public void saveClient(Client client) {
    clientRepository.save(client);
  }

  @Override
  public Client findByAppointments(Appointment appointment) {
    return clientRepository.findByAppointments(appointment);
  }

  @Override
  public List<Client> findByAppointmentsHairdresser(Hairdresser hairdresser) {
    return clientRepository.findByAppointmentsHairdresser(hairdresser);
  }

  @Override
  public Optional<Client> findClientById(Long id) {
    return clientRepository.findById(id);
  }

  @Override
  public Optional<Client> findClientByEmail(String email) {
    System.out.println("Inside findClientByEmail with email: " + email);
    Client client = clientRepository.findByEmail(email);
    System.out.println("Client found: " + client);
    return Optional.ofNullable(clientRepository.findByEmail(email));
  }

//  @Override
//  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    Client client = clientRepository.findByEmail(username);
//    if (client == null) {
//      throw new UsernameNotFoundException("Client with email " + username + " not found");
//    }
//    return client;
//  }
}