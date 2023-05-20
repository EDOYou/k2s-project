package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

  @Mock
  private ClientRepository clientRepository;

  @InjectMocks
  private ClientServiceImpl clientService;

  private Client client;


  @BeforeEach
  void setUp() {
    client = new Client();
    client.setEmail("test@test.com");
  }

  @Test
  public void testSaveClient() {
    clientService.saveClient(client);
    verify(clientRepository, times(1)).save(client);
  }

  @Test
  public void whenValidEmail_thenClientShouldBeFound() {
    when(clientRepository.findByEmail("test@test.com")).thenReturn(client);

    Optional<Client> found = clientService.findClientByEmail("test@test.com");

    assertEquals("test@test.com", found.get().getEmail());
  }

  @Test
  public void whenInValidEmail_thenClientShouldNotBeFound() {
    when(clientRepository.findByEmail("invalid@test.com")).thenReturn(null);

    Optional<Client> notFound = clientService.findClientByEmail("invalid@test.com");

    assertFalse(notFound.isPresent());
  }

}