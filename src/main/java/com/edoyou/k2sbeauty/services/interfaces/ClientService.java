package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ClientService {

  /**
   * Find a client by their appointment.
   *
   * @param appointment The appointment to search for.
   * @return The client associated with the given appointment.
   */
  Client findByAppointments(Appointment appointment);

  /**
   * Find all clients who have appointments with a specific hairdresser.
   *
   * @param hairdresser The hairdresser to search for.
   * @return A list of clients who have appointments with the given hairdresser.
   */
  List<Client> findByAppointmentsHairdresser(Hairdresser hairdresser);

  Optional<Client> findClientById(Long id);

  Optional<Client> findClientByEmail(String email);

  void saveClient(Client client);
}

