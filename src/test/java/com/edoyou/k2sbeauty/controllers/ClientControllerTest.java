package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.facade.ClientServiceFacade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

  @Mock
  private ClientServiceFacade clientServiceFacade;

  private ClientController clientController;

  @BeforeEach
  public void setUp() {
    clientController = new ClientController(clientServiceFacade);
  }

  @Test
  public void testProcessRegistrationForm() {
    Client client = new Client();
    String viewName = clientController.processRegistrationForm(client);

    assertEquals("redirect:/client/appointments", viewName);
    verify(clientServiceFacade, times(1)).processRegistrationForm(client);
  }

  @Test
  public void testShowBookingPage() {
    Model model = mock(Model.class);
    List<Hairdresser> hairdressers = new ArrayList<>();
    List<String> services = new ArrayList<>();

    when(clientServiceFacade.getHairdressersWithServices()).thenReturn(hairdressers);
    when(clientServiceFacade.getDistinctServiceNames()).thenReturn(services);

    String viewName = clientController.showBookingPage(model);

    assertEquals("client/book", viewName);
    verify(clientServiceFacade, times(1)).getHairdressersWithServices();
    verify(clientServiceFacade, times(1)).getDistinctServiceNames();
    verify(model, times(1)).addAttribute("hairdressers", hairdressers);
    verify(model, times(1)).addAttribute("services", services);
  }

  @Test
  public void testBookAppointment() {
    Authentication authentication = mock(Authentication.class);
    Long hairdresserId = 1L;
    String serviceName = "Test service";
    String dateTime = "2023-06-01 14:00:00";

    String viewName = clientController.bookAppointment(authentication, hairdresserId, serviceName,
        dateTime);

    assertEquals("redirect:/client/appointments", viewName);
    verify(clientServiceFacade, times(1)).bookAppointment(authentication, hairdresserId,
        serviceName, dateTime);
  }

  @Test
  void testGetTimeSlots() {
    Long hairdresserId = 1L;
    List<String> timeSlots = Arrays.asList("10:00", "11:00");

    when(clientServiceFacade.getTimeSlots(hairdresserId)).thenReturn(timeSlots);
    ResponseEntity<List<String>> response = clientController.getTimeSlots(hairdresserId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(timeSlots, response.getBody());
    verify(clientServiceFacade, times(1)).getTimeSlots(hairdresserId);
  }

  @Test
  void testViewAppointments() {
    Authentication authentication = mock(Authentication.class);
    Model model = mock(Model.class);
    List<Appointment> appointments = new ArrayList<>();

    when(authentication.getName()).thenReturn("client");
    when(clientServiceFacade.getClientAppointments(authentication)).thenReturn(appointments);
    String viewName = clientController.viewAppointments(authentication, model);

    assertEquals("client/appointments", viewName);
    verify(model, times(1)).addAttribute("appointments", appointments);
    verify(clientServiceFacade, times(1)).getClientAppointments(authentication);
  }

  @Test
  void testShowFeedbackForm() {
    Long appointmentId = 1L;
    Model model = mock(Model.class);

    String viewName = clientController.showFeedbackForm(appointmentId, model);

    assertEquals("client/feedback", viewName);
    verify(model, times(1)).addAttribute(eq("feedback"), any(Feedback.class));
    verify(model, times(1)).addAttribute("appointmentId", appointmentId);
  }

  @Test
  void testSubmitFeedback() {
    Authentication authentication = mock(Authentication.class);
    Feedback feedback = new Feedback();
    Long appointmentId = 1L;

    when(authentication.isAuthenticated()).thenReturn(true);
    String viewName = clientController.submitFeedback(authentication, feedback, appointmentId);

    assertEquals("redirect:/admin/appointments", viewName);
    verify(clientServiceFacade, times(1)).saveFeedback(authentication, appointmentId, feedback);
  }

  @Test
  void testShowRegistrationForm() {
    Model model = mock(Model.class);

    String viewName = clientController.showRegistrationForm(model);

    assertEquals("register", viewName);
    verify(model, times(1)).addAttribute(eq("client"), any(Client.class));
  }

  @Test
  void testShowLoginPage() {
    String viewName = clientController.showLoginPage();

    assertEquals("login", viewName);
  }

}