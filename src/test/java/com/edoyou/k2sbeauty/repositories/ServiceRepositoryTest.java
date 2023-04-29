package com.edoyou.k2sbeauty.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.edoyou.k2sbeauty.entities.model.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ServiceRepositoryTest {

  @Autowired
  private ServiceRepository serviceRepository;
  private Service service;

  @BeforeEach
  void setUp() {
    service = new Service();
    service.setName("Hair colouring");
    service.setDescription("We are colouring the hair and washing.");
    service.setPrice(10d);
  }

  @Test
  public void shouldFindByName() {
    Service savedService = serviceRepository.save(service);

    assertThat(savedService.getName()).isNotNull();
    assertThat(savedService.getId()).isNotNull();
  }

}
