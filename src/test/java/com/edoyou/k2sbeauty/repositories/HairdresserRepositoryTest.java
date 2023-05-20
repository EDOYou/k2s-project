package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest
public class HairdresserRepositoryTest {

  @Mock
  HairdresserRepository hairdresserRepository;

  @Test
  void shouldFindAllByService() {
    Long serviceId = 1L;
    Sort sort = Sort.by("lastName");
    List<Hairdresser> hairdressers = new ArrayList<>();

    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findAllByService(serviceId, sort)).thenReturn(hairdressers);

    List<Hairdresser> returnedHairdressers = hairdresserRepository.findAllByService(serviceId,
        sort);

    verify(hairdresserRepository, times(1)).findAllByService(serviceId, sort);
    Assertions.assertEquals(hairdressers.size(), returnedHairdressers.size());
  }

  @Test
  void shouldFindAllWithBeautyServices() {
    List<Hairdresser> hairdressers = new ArrayList<>();

    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findAllWithBeautyServices()).thenReturn(hairdressers);

    List<Hairdresser> returnedHairdressers = hairdresserRepository.findAllWithBeautyServices();

    verify(hairdresserRepository, times(1)).findAllWithBeautyServices();
    Assertions.assertEquals(hairdressers.size(), returnedHairdressers.size());
  }

  @Test
  void shouldFindAllWithBeautyServicesPageable() {
    Pageable pageable = PageRequest.of(0, 5);
    Page<Hairdresser> hairdresserPage = new PageImpl<>(Collections.emptyList());

    when(hairdresserRepository.findAllWithBeautyServices(pageable)).thenReturn(hairdresserPage);

    Page<Hairdresser> returnedHairdresserPage = hairdresserRepository.findAllWithBeautyServices(
        pageable);

    verify(hairdresserRepository, times(1)).findAllWithBeautyServices(pageable);
    Assertions.assertEquals(hairdresserPage.getSize(), returnedHairdresserPage.getSize());
  }

  @Test
  void shouldFindByIsApprovedPageable() {
    Pageable pageable = PageRequest.of(0, 5);
    Page<Hairdresser> hairdresserPage = new PageImpl<>(Collections.emptyList());
    boolean isApproved = true;

    when(hairdresserRepository.findByIsApproved(isApproved, pageable)).thenReturn(hairdresserPage);

    Page<Hairdresser> returnedHairdresserPage = hairdresserRepository.findByIsApproved(isApproved,
        pageable);

    verify(hairdresserRepository, times(1)).findByIsApproved(isApproved, pageable);
    Assertions.assertEquals(hairdresserPage.getSize(), returnedHairdresserPage.getSize());
  }

  @Test
  void shouldFindByIsApprovedTrue() {
    List<Hairdresser> hairdressers = new ArrayList<>();

    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findByIsApprovedTrue()).thenReturn(hairdressers);

    List<Hairdresser> returnedHairdressers = hairdresserRepository.findByIsApprovedTrue();

    verify(hairdresserRepository, times(1)).findByIsApprovedTrue();
    Assertions.assertEquals(hairdressers.size(), returnedHairdressers.size());
  }

  @Test
  void shouldFindByIsApprovedTrueSorted() {
    Sort sort = Sort.by("lastName");
    List<Hairdresser> hairdressers = new ArrayList<>();

    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findByIsApprovedTrue(sort)).thenReturn(hairdressers);

    List<Hairdresser> returnedHairdressers = hairdresserRepository.findByIsApprovedTrue(sort);

    verify(hairdresserRepository, times(1)).findByIsApprovedTrue(sort);
    Assertions.assertEquals(hairdressers.size(), returnedHairdressers.size());
  }

  @Test
  void shouldFindByIdWithAppointments() {
    Long id = 1L;
    Optional<Hairdresser> hairdresser = Optional.of(new Hairdresser());

    when(hairdresserRepository.findByIdWithAppointments(id)).thenReturn(hairdresser);

    Optional<Hairdresser> returnedHairdresser = hairdresserRepository.findByIdWithAppointments(id);

    verify(hairdresserRepository, times(1)).findByIdWithAppointments(id);
    Assertions.assertEquals(hairdresser, returnedHairdresser);
  }

}