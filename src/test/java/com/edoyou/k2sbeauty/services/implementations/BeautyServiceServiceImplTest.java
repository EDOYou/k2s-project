package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.repositories.BeautyServiceRepository;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeautyServiceServiceImplTest {

  @Mock
  private BeautyServiceRepository beautyServiceRepository;

  @InjectMocks
  private BeautyServiceServiceImpl beautyServiceService;

  private BeautyService beautyService;
  private List<BeautyService> beautyServiceList;

  @BeforeEach
  public void setUp() {
    beautyService = new BeautyService();
    beautyService.setId(1L);
    beautyService.setName("Haircut");
    beautyService.setDescription("A standard haircut");
    beautyService.setPrice(25.0);
    beautyService.setPrice(25.0);
    beautyService.setDuration(30);

    beautyServiceList = Collections.singletonList(beautyService);
  }

  @Test
  void shouldSaveService() {
    beautyServiceService.saveService(beautyService);

    verify(beautyServiceRepository, times(1)).save(beautyService);
  }

  @Test
  public void findById() {
    when(beautyServiceRepository.findById(any(Long.class))).thenReturn(Optional.of(beautyService));

    Optional<BeautyService> foundBeautyService = beautyServiceService.findById(1L);
    assertTrue(foundBeautyService.isPresent());
    assertEquals(beautyService.getId(), foundBeautyService.get().getId());
  }

  @Test
  void shouldFindAllServices() {
    when(beautyServiceRepository.findAll()).thenReturn(beautyServiceList);

    List<BeautyService> result = beautyServiceService.findAllServices();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(beautyService, result.get(0));
  }

  @Test
  void shouldFindDistinctServiceNames() {
    when(beautyServiceRepository.findDistinctServiceNames()).thenReturn(
        Collections.singletonList(beautyService.getName()));

    List<String> result = beautyServiceService.findDistinctServiceNames();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(beautyService.getName(), result.get(0));
  }

  @Test
  void shouldFindFirstByName() {
    when(beautyServiceRepository.findFirstByName(beautyService.getName())).thenReturn(
        Optional.of(beautyService));

    Optional<BeautyService> result = beautyServiceService.findFirstByName(beautyService.getName());

    assertTrue(result.isPresent());
    assertEquals(beautyService, result.get());
  }

  @Test
  void shouldFindAllByIdIn() {
    when(beautyServiceRepository.findAllById(any())).thenReturn(beautyServiceList);

    List<BeautyService> result = beautyServiceService.findAllByIdIn(
        Collections.singletonList(beautyService.getId()));

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(beautyService, result.get(0));
  }

  @Test
  void shouldFindAll() {
    when(beautyServiceRepository.findAllWithApprovedHairdressers()).thenReturn(beautyServiceList);

    List<BeautyService> result = beautyServiceService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(beautyService, result.get(0));
  }

  @Test
  void shouldFindAllPageable() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<BeautyService> beautyServicePage = new PageImpl<>(beautyServiceList, pageable,
        beautyServiceList.size());

    when(beautyServiceRepository.findAll(pageable)).thenReturn(beautyServicePage);

    Page<BeautyService> result = beautyServiceService.findAll(pageable);

    assertNotNull(result);
    assertEquals(1, result.getNumberOfElements());
    assertEquals(beautyService, result.getContent().get(0));
  }

  @Test
  void shouldReturnEmptyListWhenFindAllByIdInWithNull() {
    List<BeautyService> result = beautyServiceService.findAllByIdIn(null);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldThrowRuntimeExceptionWhenFindAllByIdInThrowsException() {
    when(beautyServiceRepository.findAllById(any())).thenThrow(new RuntimeException());

    assertThrows(RuntimeException.class,
        () -> beautyServiceService.findAllByIdIn(Collections.singletonList(beautyService.getId())));
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenFindByIdWithNullId() {
    assertThrows(IllegalArgumentException.class, () -> beautyServiceService.findById(null));

    verify(beautyServiceRepository, never()).findById(any());
  }

  @Test
  void shouldNotSaveWhenServiceIsNull() {
    assertThrows(NullPointerException.class, () -> beautyServiceService.saveService(null));

    verify(beautyServiceRepository, never()).save(any());
  }

  @Test
  void shouldReturnEmptyListWhenFindAllReturnsEmptyList() {
    when(beautyServiceRepository.findAll()).thenReturn(new ArrayList<>());

    List<BeautyService> result = beautyServiceService.findAllServices();

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldThrowRuntimeExceptionWhenFindByIdThrowsException() {
    when(beautyServiceRepository.findById(any())).thenThrow(new RuntimeException());

    assertThrows(RuntimeException.class, () -> beautyServiceService.findById(1L));
  }


}