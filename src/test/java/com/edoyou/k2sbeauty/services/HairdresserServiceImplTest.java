package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.services.implementations.HairdresserServiceImpl;
import java.util.Collections;
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
  void shouldFindBySpecialization() {
    when(hairdresserRepository.findBySpecialization(hairdresser.getSpecialization())).thenReturn(Arrays.asList(hairdresser));

    List<Hairdresser> foundHairdressers = hairdresserService.findBySpecialization(hairdresser.getSpecialization());

    assertThat(foundHairdressers).isNotEmpty();
    assertThat(foundHairdressers.size()).isEqualTo(1);
    assertThat(foundHairdressers.get(0).getSpecialization()).isEqualTo(hairdresser.getSpecialization());
  }

  @Test
  void shouldFindAllHairdressers() {
    when(hairdresserRepository.findAll()).thenReturn(Arrays.asList(hairdresser));

    List<Hairdresser> foundHairdressers = hairdresserService.findAllHairdressers(null);

    assertThat(foundHairdressers).isNotEmpty();
    assertThat(foundHairdressers.size()).isEqualTo(1);
    assertThat(foundHairdressers.get(0).getId()).isEqualTo(hairdresser.getId());
  }

  @Test
  void shouldUpdateHairdresser() {
    when(hairdresserRepository.findById(hairdresser.getId())).thenReturn(Optional.of(hairdresser));
    when(hairdresserRepository.save(any(Hairdresser.class))).thenReturn(hairdresser);

    Hairdresser updatedHairdresser = hairdresserService.updateHairdresser(hairdresser);

    assertThat(updatedHairdresser).isNotNull();
    assertThat(updatedHairdresser.getId()).isEqualTo(hairdresser.getId());
    assertThat(updatedHairdresser.getFirstName()).isEqualTo(hairdresser.getFirstName());
  }

  @Test
  void shouldDeleteHairdresser() {
    when(hairdresserRepository.findById(hairdresser.getId())).thenReturn(Optional.of(hairdresser));
    doNothing().when(hairdresserRepository).delete(hairdresser);

    hairdresserService.deleteHairdresser(hairdresser.getId());

    verify(hairdresserRepository, times(1)).delete(hairdresser);
  }

  @Test
  void shouldFindBySpecialization_EmptyResult() {
    when(hairdresserRepository.findBySpecialization("NonExistingSpecialization")).thenReturn(
        Collections.emptyList());

    List<Hairdresser> foundHairdressers = hairdresserService.findBySpecialization("NonExistingSpecialization");

    assertThat(foundHairdressers).isEmpty();
  }

  @Test
  void shouldFindAllHairdressers_SortedByName() {
    when(hairdresserRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName"))).thenReturn(Arrays.asList(hairdresser));

    List<Hairdresser> foundHairdressers = hairdresserService.findAllHairdressers("lastName");

    assertThat(foundHairdressers).isNotEmpty();
    assertThat(foundHairdressers.size()).isEqualTo(1);
    assertThat(foundHairdressers.get(0).getId()).isEqualTo(hairdresser.getId());
  }

  @Test
  void shouldFindAllHairdressers_SortedByRating() {
    when(hairdresserRepository.findAll(Sort.by(Sort.Direction.DESC, "rating"))).thenReturn(Arrays.asList(hairdresser));

    List<Hairdresser> foundHairdressers = hairdresserService.findAllHairdressers("rating");

    assertThat(foundHairdressers).isNotEmpty();
    assertThat(foundHairdressers.size()).isEqualTo(1);
    assertThat(foundHairdressers.get(0).getId()).isEqualTo(hairdresser.getId());
  }

  @Test
  void shouldThrowIllegalArgumentException_InvalidSortByValue() {
    assertThrows(IllegalArgumentException.class, () -> hairdresserService.findAllHairdressers("invalidSortByValue"));
  }
}