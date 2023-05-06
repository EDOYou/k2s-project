package com.edoyou.k2sbeauty.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.ArrayList;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

// Integration with JUnit 5
@ExtendWith(SpringExtension.class)
// Web layer testing only
@WebMvcTest(GuestController.class)
// Disabling default security filters for testing
@AutoConfigureMockMvc(addFilters = false)
public class GuestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BeautyServiceService beautyServiceService;

  @MockBean
  private HairdresserService hairdresserService;

  // Testing the /guest/services endpoint without any request parameters
  @Test
  public void viewServices_noParams_returnsServicesView() throws Exception {
    // Arrange: Set up the expected behavior for the mocked dependencies
    when(beautyServiceService.findAll()).thenReturn(new ArrayList<>());
    when(hairdresserService.findAllHairdressers("lastName")).thenReturn(new ArrayList<>());

    // Act & Assert: Performing the request and check the expected outcomes
    mockMvc.perform(get("/guest/services")).andExpect(status().isOk())
        .andExpect(view().name("/guest/services")).andExpect(
            model().attributeExists("services", "hairdressers", "sort"))
        .andExpect(model().attribute("selectedHairdresser", Matchers.nullValue()))
        .andExpect(model().attribute("selectedService", Matchers.nullValue()));
  }

  @Test
  public void viewServices_withParams_returnsServicesView() throws Exception {
    when(beautyServiceService.findAll()).thenReturn(new ArrayList<>());
    when(hairdresserService.findAllHairdressers("rating")).thenReturn(new ArrayList<>());

    mockMvc.perform(get("/guest/services").param("hairdresser", "1").param("service", "2")
            .param("sort", "rating")).andExpect(status().isOk()).andExpect(view().name("/guest/services"))
        .andExpect(model().attributeExists("services", "hairdressers", "selectedHairdresser",
            "selectedService", "sort")).andExpect(model().attribute("selectedHairdresser", 1L))
        .andExpect(model().attribute("selectedService", 2L))
        .andExpect(model().attribute("sort", "rating"));
  }
}