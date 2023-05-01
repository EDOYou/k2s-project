package com.edoyou.k2sbeauty.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class BeautyServiceRepositoryTest {

  @Autowired
  private BeautyServiceRepository beautyServiceRepository;
  private BeautyService beautyService;

  @BeforeEach
  void setUp() {
    beautyService = new BeautyService();
    beautyService.setName("Hair colouring");
    beautyService.setDescription("We are colouring the hair and washing.");
    beautyService.setPrice(10d);
  }

  @Test
  public void shouldFindByName() {
    BeautyService savedBeautyService = beautyServiceRepository.save(beautyService);

    assertThat(savedBeautyService.getName()).isNotNull();
    assertThat(savedBeautyService.getId()).isNotNull();
  }

}
