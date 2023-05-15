package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.services.implementations.HairdresserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HairdresserServiceImplTest {

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
  void shouldDeleteHairdresser() {
    when(hairdresserRepository.findById(hairdresser.getId())).thenReturn(Optional.of(hairdresser));
    doNothing().when(hairdresserRepository).delete(hairdresser);

    hairdresserService.deleteHairdresser(hairdresser.getId());

    verify(hairdresserRepository, times(1)).delete(hairdresser);
  }

  @Test
  void shouldThrowIllegalArgumentException_InvalidSortByValue() {
    assertThrows(IllegalArgumentException.class, () -> hairdresserService.findAllHairdressers("invalidSortByValue"));
  }
}