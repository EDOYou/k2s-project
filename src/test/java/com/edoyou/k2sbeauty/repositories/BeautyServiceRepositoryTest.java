package com.edoyou.k2sbeauty.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
public class BeautyServiceRepositoryTest {


  @Autowired
  private BeautyServiceRepository beautyServiceRepository;

  @Autowired
  private HairdresserRepository hairdresserRepository;

  @BeforeEach
  void setUp() {
    Hairdresser hairdresser1 = new Hairdresser();
    hairdresser1.setId(1L);
    hairdresser1.setFirstName("Kanan");
    hairdresser1.setLastName("Taghiyev");
    hairdresser1.setPassword("123");
    hairdresser1.setPhone("12345");
    hairdresser1.setSpecialization("Colorist");
    hairdresser1.setEmail("hairdresser1@example.com");
    hairdresser1.setApproved(true);
    hairdresserRepository.save(hairdresser1);

    Hairdresser hairdresser2 = new Hairdresser();
    hairdresser2.setId(1L);
    hairdresser2.setFirstName("Kanan");
    hairdresser2.setLastName("Taghiyev");
    hairdresser2.setPassword("123");
    hairdresser2.setPhone("12345");
    hairdresser2.setSpecialization("Colorist");
    hairdresser2.setEmail("hairdresser2@example.com");
    hairdresser2.setApproved(false);
    hairdresserRepository.save(hairdresser2);

    BeautyService service1 = new BeautyService();
    service1.setName("Service1");
    service1.setDescription("It is service 1");
    service1.setPrice(90);
    service1.setHairdressers(new HashSet<>(List.of(hairdresser1)));
    beautyServiceRepository.save(service1);

    BeautyService service2 = new BeautyService();
    service2.setName("Service2");
    service2.setDescription("It is service 2");
    service2.setPrice(91);
    service2.setHairdressers(new HashSet<>(Arrays.asList(hairdresser1, hairdresser2)));
    beautyServiceRepository.save(service2);
  }


  @Test
  public void findDistinctServiceNames_ShouldReturnDistinctServiceNames() {
    List<String> names = beautyServiceRepository.findDistinctServiceNames();

    assertEquals(2, names.size());
    assertTrue(names.contains("Service1"));
    assertTrue(names.contains("Service2"));
  }

  @Test
  public void findFirstByName_ShouldReturnFirstServiceWithGivenName() {
    String serviceName = "Service1";
    BeautyService serviceOpt = beautyServiceRepository.findFirstByName(serviceName).orElse(null);

    assertNotNull(serviceOpt);
    assertEquals(serviceName, serviceOpt.getName());
  }

}