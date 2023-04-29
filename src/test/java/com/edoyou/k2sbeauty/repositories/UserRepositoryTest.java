package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  private Client client;
  private Hairdresser hairdresser;

  @BeforeEach
  void setUp() {
    client = new Client();
    client.setFirstName("Barbara");
    client.setLastName("O'Conner");
    client.setEmail("myemail@gmail.com");
    client.setPassword("password");
    client.setPhone("36707733373");
    client.setCreatedAt(LocalDateTime.now());
    client.setUpdatedAt(LocalDateTime.now());

    hairdresser = new Hairdresser();
    hairdresser.setFirstName("Jane");
    hairdresser.setLastName("Doe");
    hairdresser.setEmail("jane.doe@gmail.com");
    hairdresser.setPassword("password");
    hairdresser.setPhone("36707733444");
    hairdresser.setCreatedAt(LocalDateTime.now());
    hairdresser.setUpdatedAt(LocalDateTime.now());
    hairdresser.setSpecialization("Hairstyling");
  }

  @Test
  public void shouldSaveClient() {
    Client savedClient = userRepository.save(client);
    assertThat(savedClient).isNotNull();
    assertThat(savedClient.getId()).isNotNull();
  }

  @Test
  public void shouldSaveHairdresser() {
    Hairdresser savedHairdresser = userRepository.save(hairdresser);
    System.out.println(savedHairdresser.getId());
    assertThat(savedHairdresser).isNotNull();
    assertThat(savedHairdresser.getId()).isNotNull();
  }

  @Test
  public void shouldFindClientByEmail() {
    userRepository.save(client);

    Optional<User> foundClient = userRepository.findByEmail(client.getEmail());
    assertThat(foundClient.isPresent()).isTrue();
    assertThat(foundClient.get().getFirstName()).isEqualTo(client.getFirstName());
    assertThat(foundClient.get().getLastName()).isEqualTo(client.getLastName());
  }

}
