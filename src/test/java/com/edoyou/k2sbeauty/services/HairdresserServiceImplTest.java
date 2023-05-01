package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.implementations.HairdresserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HairdresserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private HairdresserRepository hairdresserRepository;

  @InjectMocks
  private HairdresserServiceImpl hairdresserService;

  private Hairdresser hairdresser;

  @BeforeEach
  void setUp() {
    hairdresser = new Hairdresser();
    hairdresser.setId(1L);
    hairdresser.setFirstName("John");
    hairdresser.setLastName("Doe");
    hairdresser.setEmail("johndoe@gmail.com");
    hairdresser.setPassword("password");
    hairdresser.setPhone("1234567890");
    hairdresser.setSpecialization("Colorist");
  }

  @Test
  void shouldFindBySpecialization() {
    when(hairdresserRepository.findBySpecialization(hairdresser.getSpecialization())).thenReturn(Arrays.asList(hairdresser));

    List<Hairdresser> foundHairdressers = hairdresserService.findBySpecialization(hairdresser.getSpecialization());

    assertThat(foundHairdressers).isNotEmpty();
    assertThat(foundHairdressers.size()).isEqualTo(1);
    assertThat(foundHairdressers.get(0).getSpecialization()).isEqualTo(hairdresser.getSpecialization());
  }
}
