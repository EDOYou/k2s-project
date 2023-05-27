package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.services.implementations.appointment_details.TimeSlotService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceFacadeTest {

  @Mock
  private ClientService clientService;
  @Mock
  private HairdresserService hairdresserService;
  @Mock
  private BeautyServiceService beautyServiceService;
  @Mock
  private AppointmentService appointmentService;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private RoleRepository roleRepository;
  @Mock
  private TimeSlotService timeSlotService;
  @Mock
  private FeedbackService feedbackService;

  @InjectMocks
  private ClientServiceFacade clientServiceFacade;

  @Test
  void testProcessRegistrationForm() {
    Client client = new Client();
    client.setPassword("123");
    Role role = new Role();
    role.setName("ROLE_CLIENT");

    when(passwordEncoder.encode("123")).thenReturn("encoded_password");
    when(roleRepository.findByName("ROLE_CLIENT")).thenReturn(Optional.of(role));

    clientServiceFacade.processRegistrationForm(client);

    assertEquals("encoded_password", client.getPassword());
    verify(clientService, times(1)).saveClient(client);
  }

  @Test
  void testGetHairdressersWithServices() {
    List<Hairdresser> expectedHairdressers = List.of(new Hairdresser());
    when(hairdresserService.findAllWithBeautyServices()).thenReturn(expectedHairdressers);

    List<Hairdresser> actualHairdressers = clientServiceFacade.getHairdressersWithServices();

    assertEquals(expectedHairdressers, actualHairdressers);
  }

  @Test
  void testGetDistinctServiceNames() {
    List<String> expectedServiceNames = List.of("service1", "service2");
    when(beautyServiceService.findDistinctServiceNames()).thenReturn(expectedServiceNames);

    List<String> actualServiceNames = clientServiceFacade.getDistinctServiceNames();

    assertEquals(expectedServiceNames, actualServiceNames);
  }

  @Test
  void testBookAppointment() {
    String email = "email@test.com";
    String serviceName = "service1";
    Long hairdresserId = 1L;
    String dateTime = "2023-05-20T14:30 - 2023-05-20T15:30";
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setId(hairdresserId);
    BeautyService beautyService = new BeautyService();
    beautyService.setName(serviceName);
    Client client = new Client();
    client.setEmail(email);
    Authentication auth = mock(Authentication.class);

    when(auth.getName()).thenReturn(email);
    when(hairdresserService.findById(hairdresserId)).thenReturn(hairdresser);
    when(beautyServiceService.findFirstByName(serviceName)).thenReturn(Optional.of(beautyService));
    when(clientService.findClientByEmail(email)).thenReturn(Optional.of(client));

    clientServiceFacade.bookAppointment(auth, hairdresserId, serviceName, dateTime);

    verify(appointmentService).saveAppointment(any(Appointment.class));
  }

  @Test
  void getClientAppointmentsTest() {
    User user = new User();
    user.setEmail("user@mail.com");
    user.setPassword("123");
    user.setRoles(new HashSet<>());

    Authentication auth = new TestingAuthenticationToken(user, null);

    Client client = new Client();
    client.setEmail("user@mail.com");

    when(clientService.findClientByEmail("user@mail.com"))
        .thenReturn(Optional.of(client));
    when(appointmentService.findByClient(client))
        .thenReturn(List.of(new Appointment(), new Appointment()));

    List<Appointment> result = clientServiceFacade.getClientAppointments(auth);

    assertEquals(2, result.size());
    verify(clientService, times(1)).findClientByEmail(anyString());
    verify(appointmentService, times(1)).findByClient(any(Client.class));
  }

  @Test
  void saveFeedbackTest() {
    User user = new User();
    user.setEmail("user@mail.com");
    user.setPassword("123");
    user.setRoles(new HashSet<>());

    Authentication auth = new TestingAuthenticationToken(user, null);

    Client client = new Client();
    client.setEmail("user@mail.com");
    Appointment appointment = new Appointment();
    Feedback feedback = new Feedback();

    when(clientService.findClientByEmail("user@mail.com")).thenReturn(Optional.of(client));
    when(appointmentService.findById(anyLong())).thenReturn(Optional.of(appointment));

    clientServiceFacade.saveFeedback(auth, 1L, feedback);

    assertEquals(client, feedback.getClient());
    assertEquals(appointment, feedback.getAppointment());
    assertNotNull(feedback.getCreatedAt());
    verify(feedbackService, times(1)).createFeedback(any(Feedback.class));
  }

  @Test
  void getTimeSlotsTest() {
    Long hairdresserId = 1L;
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setId(hairdresserId);

    LocalDateTime start1 = LocalDateTime.of(2023, 5, 26, 10, 30);
    LocalDateTime end1 = LocalDateTime.of(2023, 5, 26, 11, 30);
    LocalDateTime start2 = LocalDateTime.of(2023, 5, 26, 12, 0);
    LocalDateTime end2 = LocalDateTime.of(2023, 5, 26, 13, 0);

    TimeSlot timeSlot1 = new TimeSlot(start1, end1, new Appointment());
    TimeSlot timeSlot2 = new TimeSlot(start2, end2, new Appointment());

    when(hairdresserService.findById(hairdresserId)).thenReturn(hairdresser);
    when(timeSlotService.generateTimeSlots(hairdresser)).thenReturn(List.of(timeSlot1, timeSlot2));

    List<String> actualTimeSlots = clientServiceFacade.getTimeSlots(hairdresserId);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    List<String> expectedTimeSlots = List.of(
        formatter.format(start1) + " - " + formatter.format(end1),
        formatter.format(start2) + " - " + formatter.format(end2)
    );

    assertEquals(expectedTimeSlots, actualTimeSlots);
  }

}