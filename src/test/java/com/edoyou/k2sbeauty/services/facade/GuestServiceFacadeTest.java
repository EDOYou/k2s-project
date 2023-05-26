package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.exceptions.BeautyServiceNotFoundException;
import com.edoyou.k2sbeauty.exceptions.HairdresserNotFoundException;
import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GuestServiceFacadeTest {

  private BeautyServiceService beautyServiceService;
  private HairdresserService hairdresserService;
  private GuestServiceFacade guestServiceFacade;

  @BeforeEach
  public void setUp() {
    beautyServiceService = Mockito.mock(BeautyServiceService.class);
    hairdresserService = Mockito.mock(HairdresserService.class);
    guestServiceFacade = new GuestServiceFacade(beautyServiceService, hairdresserService);
  }

  @Test
  public void getServicesData_forHairdresser() {
    Hairdresser mockHairdresser = new Hairdresser();
    Set<BeautyService> mockServices = new HashSet<>();
    mockHairdresser.setBeautyServices(mockServices);

    when(hairdresserService.findById(anyLong())).thenReturn(mockHairdresser);

    guestServiceFacade.getServicesData(1L, null, "serviceName");

    verify(hairdresserService).findById(anyLong());
  }

  @Test
  public void getServicesData_forService() {
    BeautyService mockService = new BeautyService();
    Set<Hairdresser> mockHairdressers = new HashSet<>();
    mockService.setHairdressers(mockHairdressers);

    when(beautyServiceService.findById(anyLong())).thenReturn(Optional.of(mockService));

    guestServiceFacade.getServicesData(null, 1L, "serviceName");

    verify(beautyServiceService).findById(anyLong());
  }

  @Test
  public void getServicesData_forAll() {
    when(beautyServiceService.findAll()).thenReturn(new ArrayList<>());
    when(hairdresserService.findAllHairdressers(anyString())).thenReturn(new ArrayList<>());

    guestServiceFacade.getServicesData(null, null, "serviceName");

    verify(beautyServiceService).findAll();
    verify(hairdresserService).findAllHairdressers(anyString());
  }

  @Test
  public void getServicesData_withDifferentSortBy() {
    when(beautyServiceService.findAll()).thenReturn(new ArrayList<>());
    when(hairdresserService.findAllHairdressers(anyString())).thenReturn(new ArrayList<>());

    guestServiceFacade.getServicesData(null, null, "serviceName");
    guestServiceFacade.getServicesData(null, null, "lastName");
    guestServiceFacade.getServicesData(null, null, "rating");

    verify(beautyServiceService, times(3)).findAll();
    verify(hairdresserService, times(3)).findAllHairdressers(anyString());
  }

  @Test
  public void getServicesData_withInvalidSortBy() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> guestServiceFacade.getServicesData(null, null, "invalidSortBy"));

    assertEquals("Invalid sortBy value: invalidSortBy", exception.getMessage());
  }

  @Test
  public void getServicesDataForAll_withEmptyResult() {
    when(beautyServiceService.findAll()).thenReturn(new ArrayList<>());
    when(hairdresserService.findAllHairdressers(anyString())).thenReturn(new ArrayList<>());

    ServicesData servicesData = guestServiceFacade.getServicesData(null, null, "serviceName");

    assertNotNull(servicesData);
    assertTrue(servicesData.getServiceHairdresserPairs().isEmpty());
  }

  @Test
  public void getServicesDataForHairdresser_withNonexistentId() {
    when(hairdresserService.findById(anyLong())).thenReturn(null);

    Exception exception = assertThrows(HairdresserNotFoundException.class,
        () -> guestServiceFacade.getServicesData(1L, null, "serviceName"));

    assertEquals("Hairdresser not found.", exception.getMessage());
  }

  @Test
  public void getServicesDataForService_withNonexistentId() {
    when(beautyServiceService.findById(anyLong())).thenReturn(Optional.empty());

    Exception exception = assertThrows(BeautyServiceNotFoundException.class,
        () -> guestServiceFacade.getServicesData(null, 1L, "serviceName"));

    assertEquals("Service not found.", exception.getMessage());
  }

  @Test
  public void getServiceComparator_withInvalidSortBy() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> guestServiceFacade.getServicesData(null, null, "invalidSortBy"));

    assertEquals("Invalid sortBy value: invalidSortBy", exception.getMessage());
  }

}