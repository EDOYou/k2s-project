package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.facade.GuestServiceFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GuestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GuestServiceFacade guestServiceFacade;

  private List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs;
  private List<BeautyService> services;
  private List<Hairdresser> hairdressers;
  private ServicesData servicesData;

  @BeforeEach
  public void setup() {
    serviceHairdresserPairs = new ArrayList<>();
    services = new ArrayList<>();
    hairdressers = new ArrayList<>();

    servicesData = new ServicesData(serviceHairdresserPairs, services, hairdressers, "lastName",
        null, null);
    when(guestServiceFacade.getServicesData(any(), any(), any()))
        .thenReturn(servicesData);

  }

  @Test
  public void testViewServices() throws Exception {
    mockMvc.perform(get("/guest/services"))
        .andExpect(status().isOk())
        .andExpect(view().name("/guest/services"))
        .andExpect(model().attribute("serviceHairdresserPairs", serviceHairdresserPairs))
        .andExpect(model().attribute("services", services))
        .andExpect(model().attribute("hairdressers", hairdressers))
        .andExpect(model().attribute("sort", "lastName"));
  }

  @Test
  public void testViewServicesForHairdresser() throws Exception {
    when(guestServiceFacade.getServicesData(anyLong(), eq(null), any()))
        .thenReturn(servicesData);

    mockMvc.perform(get("/guest/services").param("hairdresserId", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("/guest/services"))
        .andExpect(model().attribute("serviceHairdresserPairs", serviceHairdresserPairs))
        .andExpect(model().attribute("services", services))
        .andExpect(model().attribute("hairdressers", hairdressers))
        .andExpect(model().attribute("sort", "lastName"));
  }

  @Test
  public void testViewServicesForService() throws Exception {
    when(guestServiceFacade.getServicesData(eq(null), anyLong(), any()))
        .thenReturn(servicesData);

    mockMvc.perform(get("/guest/services").param("serviceId", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("/guest/services"))
        .andExpect(model().attribute("serviceHairdresserPairs", serviceHairdresserPairs))
        .andExpect(model().attribute("services", services))
        .andExpect(model().attribute("hairdressers", hairdressers))
        .andExpect(model().attribute("sort", "lastName"));
  }

}