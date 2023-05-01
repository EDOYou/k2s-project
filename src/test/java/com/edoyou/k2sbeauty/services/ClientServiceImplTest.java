package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.ClientRepository;
import com.edoyou.k2sbeauty.services.implementations.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

  @Mock
  private ClientRepository clientRepository;

  @InjectMocks
  private ClientServiceImpl clientService;

  private Client client;
  private Appointment appointment;
  private Hairdresser hairdresser;

  @BeforeEach
  void setUp() {
    client = new Client();
    appointment = new Appointment();
    hairdresser = new Hairdresser();
  }

  @Test
  void shouldFindByAppointments() {
    when(clientRepository.findByAppointments(appointment)).thenReturn(client);

    Client foundClient = clientService.findByAppointments(appointment);

    assertThat(foundClient).isNotNull();
    assertThat(foundClient).isEqualTo(client);
    verify(clientRepository, times(1)).findByAppointments(appointment);
  }

  @Test
  void shouldFindByAppointmentsHairdresser() {
    List<Client> clients = new ArrayList<>();
    clients.add(client);
    when(clientRepository.findByAppointmentsHairdresser(hairdresser)).thenReturn(clients);

    List<Client> foundClients = clientService.findByAppointmentsHairdresser(hairdresser);

    assertThat(foundClients).isNotEmpty();
    assertThat(foundClients.size()).isEqualTo(1);
    assertThat(foundClients.get(0)).isEqualTo(client);
    verify(clientRepository, times(1)).findByAppointmentsHairdresser(hairdresser);
  }

}
