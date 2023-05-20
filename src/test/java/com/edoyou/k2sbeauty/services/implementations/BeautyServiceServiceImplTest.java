package com.edoyou.k2sbeauty.services.implementations;

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
  public void findById() {
    when(beautyServiceRepository.findById(any(Long.class))).thenReturn(Optional.of(beautyService));

    Optional<BeautyService> foundBeautyService = beautyServiceServiceImpl.findById(1L);
    assertTrue(foundBeautyService.isPresent());
    assertEquals(beautyService.getId(), foundBeautyService.get().getId());
  }

}