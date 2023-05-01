package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.ClientRepository;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <strong>ClientServiceImpl</strong> is a class that implements
 * the {@link com.edoyou.k2sbeauty.services.interfaces.ClientService} interface.
 * It provides methods to interact with Client entity in the application,
 * such as finding clients by their appointments or by the hairdresser
 * they have appointments with.
 *
 * @see Client
 */
@Service
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;

  @Autowired
  public ClientServiceImpl(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Override
  // TODO: Find a client by their appointment
  public Client findByAppointments(Appointment appointment) {
    return clientRepository.findByAppointments(appointment);
  }

  @Override
  // TODO: Find all clients who have appointments with a specific hairdresser
  public List<Client> findByAppointmentsHairdresser(Hairdresser hairdresser) {
    return clientRepository.findByAppointmentsHairdresser(hairdresser);
  }
}