package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.services.facade.AdminServiceFacade;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AdminServiceFacade adminServiceFacade;

  @MockBean
  private Authentication authentication;

  @BeforeEach
  public void setup() {
    Client client1 = new Client();
    client1.setLastName("Taghiyev");

    Hairdresser hairdresser1 = new Hairdresser();
    hairdresser1.setFirstName("Kanan");

    Appointment appointment1 = new Appointment();
    appointment1.setClient(client1);
    appointment1.setHairdresser(hairdresser1);
    appointment1.setAppointmentTime(LocalDateTime.now());
    appointment1.setPaymentStatus(PaymentStatus.PENDING);
    appointment1.setId(1L);

    Appointment appointment2 = new Appointment();
    appointment2.setClient(client1);
    appointment2.setHairdresser(hairdresser1);
    appointment2.setAppointmentTime(LocalDateTime.now().plusHours(2));
    appointment2.setPaymentStatus(PaymentStatus.PENDING);
    appointment2.setId(2L);

    Page<Appointment> appointments = new PageImpl<>(List.of(appointment1, appointment2));
    when(adminServiceFacade.findAllAppointments(any())).thenReturn(appointments);

    List<BeautyService> beautyServices = List.of(new BeautyService(), new BeautyService());
    List<Hairdresser> hairdressers = List.of(new Hairdresser(), new Hairdresser());
    Page<Hairdresser> hairdresserPage = new PageImpl<>(hairdressers);

    when(authentication.isAuthenticated()).thenReturn(true);
    when(adminServiceFacade.findAllBeautyServices()).thenReturn(beautyServices);
    when(adminServiceFacade.findAllHairdressers()).thenReturn(hairdressers);
    when(adminServiceFacade.findHairdressersWithServices(any())).thenReturn(hairdresserPage);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void testAdminDashboard() throws Exception {
    mockMvc.perform(get("/admin/dashboard")).andExpect(status().isOk())
        .andExpect(view().name("admin/dashboard"))
        .andExpect(model().attribute("appointments", hasProperty("content", hasSize(2))));

    verify(adminServiceFacade, times(1)).findAllAppointments(any());
  }

  @Test
  public void testChangeTimeSlot() throws Exception {
    mockMvc.perform(
            post("/admin/changeTimeSlot/1").param("newTimeSlot", "2023-06-25T13:00:00").with(csrf()))
        .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/dashboard"));

    verify(adminServiceFacade, times(1)).changeTimeSlot(anyLong(), anyString());
  }

  @Test
  public void testAcceptPayment() throws Exception {
    mockMvc.perform(post("/admin/acceptPayment/1").with(csrf()))
        .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/dashboard"));

    verify(adminServiceFacade, times(1)).updatePaymentStatus(anyLong());
  }

  @Test
  public void testCancelAppointment() throws Exception {
    mockMvc.perform(post("/admin/cancelAppointment/1").with(csrf()))
        .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/dashboard"));

    verify(adminServiceFacade, times(1)).cancelAppointment(anyLong());
  }

  @Test
  public void testApproveHairdresser() throws Exception {
    mockMvc.perform(post("/admin/approveHairdresser/1").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/hairdresserRegistrationReview"));

    verify(adminServiceFacade, times(1)).approveHairdresser(anyLong());
  }

  @Test
  public void testRejectHairdresser() throws Exception {
    mockMvc.perform(post("/admin/rejectHairdresser/1").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/hairdresserRegistrationReview"));

    verify(adminServiceFacade, times(1)).rejectHairdresser(anyLong());
  }

  @Test
  public void testReviewHairdresserRegistration() throws Exception {
    // Setup
    Page<Hairdresser> hairdresserPage = new PageImpl<>(
        List.of(new Hairdresser(), new Hairdresser()));
    when(adminServiceFacade.findAllYetNotApproved(anyBoolean(), any())).thenReturn(hairdresserPage);

    // Act & Assert
    mockMvc.perform(get("/admin/hairdresserRegistrationReview"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin/hairdresserRegistrationReview"))
        .andExpect(model().attribute("hairdressers", hasProperty("content", hasSize(2))));

    verify(adminServiceFacade, times(1)).findAllYetNotApproved(anyBoolean(), any());
  }

  @Test
  public void testHandleAddService() throws Exception {
    mockMvc.perform(post("/admin/add_service").with(csrf())).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/services"));

    verify(adminServiceFacade, times(1)).saveService(any());
  }

  @Test
  public void testShowServices() throws Exception {
    mockMvc.perform(get("/admin/services")).andExpect(status().isOk())
        .andExpect(view().name("admin/services"))
        .andExpect(model().attribute("services", hasSize(2)));

    verify(adminServiceFacade, times(1)).findAllBeautyServices();
  }

  @Test
  public void testHandleAssignService() throws Exception {
    mockMvc.perform(post("/admin/assign_service").with(csrf()).param("hairdresserId", "1")
            .param("serviceId", "1")).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/assign_service"));

    verify(adminServiceFacade, times(1)).assignServiceToHairdresser(anyLong(), anyLong());
  }

  @Test
  public void testShowHairdressers() throws Exception {
    mockMvc.perform(get("/admin/hairdressers")).andExpect(status().isOk())
        .andExpect(view().name("admin/hairdressers"))
        .andExpect(model().attribute("hairdressers", hasProperty("content", hasSize(2))));

    verify(adminServiceFacade, times(1)).findHairdressersWithServices(any());
  }

}