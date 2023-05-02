package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.repositories.BeautyServiceRepository;
import com.edoyou.k2sbeauty.services.implementations.BeautyServiceServiceImpl;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeautyServiceServiceImplTest {

  @Mock
  private BeautyServiceRepository beautyServiceRepository;

  @InjectMocks
  private BeautyServiceServiceImpl beautyServiceServiceImpl;

  private BeautyService beautyService;

  @BeforeEach
  public void setUp() {
    beautyService = new BeautyService();
    beautyService.setId(1L);
    beautyService.setName("Haircut");
    beautyService.setDescription("A standard haircut");
    beautyService.setPrice(25.0);
  }

  @Test
  public void createBeautyService() {
    when(beautyServiceRepository.save(any(BeautyService.class))).thenReturn(beautyService);

    BeautyService createdBeautyService = beautyServiceServiceImpl.createBeautyService(
        beautyService);
    assertNotNull(createdBeautyService);
    assertEquals(beautyService.getId(), createdBeautyService.getId());
  }

  @Test
  public void updateBeautyService() {
    when(beautyServiceRepository.findById(any(Long.class))).thenReturn(Optional.of(beautyService));
    when(beautyServiceRepository.save(any(BeautyService.class))).thenReturn(beautyService);

    BeautyService updatedBeautyService = beautyServiceServiceImpl.updateBeautyService(1L,
        beautyService);
    assertNotNull(updatedBeautyService);
    assertEquals(beautyService.getId(), updatedBeautyService.getId());
  }

  @Test
  public void deleteBeautyService() {
    when(beautyServiceRepository.existsById(any(Long.class))).thenReturn(true);

    beautyServiceServiceImpl.deleteBeautyService(1L);
    verify(beautyServiceRepository, times(1)).deleteById(any(Long.class));
  }

  @Test
  public void findById() {
    when(beautyServiceRepository.findById(any(Long.class))).thenReturn(Optional.of(beautyService));

    Optional<BeautyService> foundBeautyService = beautyServiceServiceImpl.findById(1L);
    assertTrue(foundBeautyService.isPresent());
    assertEquals(beautyService.getId(), foundBeautyService.get().getId());
  }

  @Test
  public void findAll() {
    when(beautyServiceRepository.findAll()).thenReturn(Collections.singletonList(beautyService));

    List<BeautyService> beautyServices = beautyServiceServiceImpl.findAll();
    assertNotNull(beautyServices);
    assertEquals(1, beautyServices.size());
  }

  @Test
  public void findByPriceRange() {
    when(beautyServiceRepository.findByPriceBetween(any(Double.class), any(Double.class)))
        .thenReturn(Collections.singletonList(beautyService));

    List<BeautyService> beautyServices = beautyServiceServiceImpl.findByPriceRange(20.0, 30.0);
    assertNotNull(beautyServices);
    assertEquals(1, beautyServices.size());
    assertEquals(beautyService.getId(), beautyServices.get(0).getId());
  }

  @Test
  public void createBeautyService_withNullName_throwsIllegalArgumentException() {
    beautyService.setName(null);

    assertThrows(
        IllegalArgumentException.class,
        () -> beautyServiceServiceImpl.createBeautyService(beautyService)
    );
  }

  @Test
  public void createBeautyService_withEmptyName_throwsIllegalArgumentException() {
    beautyService.setName("");

    assertThrows(
        IllegalArgumentException.class,
        () -> beautyServiceServiceImpl.createBeautyService(beautyService)
    );
  }

  @Test
  public void createBeautyService_withNullDescription_throwsIllegalArgumentException() {
    beautyService.setDescription(null);

    assertThrows(
        IllegalArgumentException.class,
        () -> beautyServiceServiceImpl.createBeautyService(beautyService)
    );
  }

  @Test
  public void createBeautyService_withEmptyDescription_throwsIllegalArgumentException() {
    beautyService.setDescription("");

    assertThrows(
        IllegalArgumentException.class,
        () -> beautyServiceServiceImpl.createBeautyService(beautyService)
    );
  }

  @Test
  public void updateBeautyService_withInvalidId_throwsIllegalArgumentException() {
    when(beautyServiceRepository.findById(any(Long.class))).thenReturn(Optional.empty());

    assertThrows(
        IllegalStateException.class,
        () -> beautyServiceServiceImpl.updateBeautyService(1L, beautyService)
    );
  }

  @Test
  public void deleteBeautyService_withInvalidId_throwsIllegalArgumentException() {
    when(beautyServiceRepository.existsById(any(Long.class))).thenReturn(false);

    assertThrows(
        IllegalStateException.class,
        () -> beautyServiceServiceImpl.deleteBeautyService(1L)
    );
  }
}