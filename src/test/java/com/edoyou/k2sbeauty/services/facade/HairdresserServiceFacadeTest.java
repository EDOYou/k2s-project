package com.edoyou.k2sbeauty.services.facade;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.edoyou.k2sbeauty.dto.AppointmentDTO;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.exceptions.AppointmentNotFoundException;
import com.edoyou.k2sbeauty.exceptions.UnauthorizedActionException;
import com.edoyou.k2sbeauty.exceptions.UserNotFoundException;
import com.edoyou.k2sbeauty.services.interfaces.*;
import com.edoyou.k2sbeauty.services.implementations.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class HairdresserServiceFacadeTest {

  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private RoleService roleService;
  @Mock
  private BeautyServiceService beautyServiceService;
  @Mock
  private HairdresserService hairdresserService;
  @Mock
  private AppointmentService appointmentService;
  @Mock
  private WorkingHoursService workingHoursService;
  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private HairdresserServiceFacade hairdresserServiceFacade;

  @BeforeEach
  public void setUp() {
    hairdresserServiceFacade = new HairdresserServiceFacade(passwordEncoder, roleService,
        beautyServiceService, hairdresserService, appointmentService, workingHoursService,
        notificationService);
  }

  @Test
  public void testGetSchedule() {
    String email = "test@mail.com";
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail(email);

    LocalDate date = LocalDate.now();
    List<TimeSlot> timeSlots = new ArrayList<>();
    Map<LocalDate, List<TimeSlot>> schedule = new HashMap<>();
    schedule.put(date, timeSlots);

    when(hairdresserService.findUserByEmail(email)).thenReturn(Optional.of(hairdresser));
    when(hairdresserService.generateSchedule(hairdresser)).thenReturn(schedule);

    Map<LocalDate, List<TimeSlot>> result = hairdresserServiceFacade.getSchedule(email);

    Assertions.assertEquals(schedule, result);
  }

  @Test
  public void testRegisterHairdresser() {
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail("test@mail.com");
    hairdresser.setPassword("password");
    Map<Integer, WorkingHoursDTO> workingHoursDtoMap = new HashMap<>();

    when(passwordEncoder.encode(hairdresser.getPassword())).thenReturn("encoded_password");
    when(roleService.getRoleByName("ROLE_HAIRDRESSER")).thenReturn(Optional.of(new Role()));
    when(beautyServiceService.findAllByIdIn(any())).thenReturn(new ArrayList<>());

    hairdresserServiceFacade.registerHairdresser(hairdresser, workingHoursDtoMap);

    verify(hairdresserService).saveHairdresser(hairdresser);
    Assertions.assertEquals("encoded_password", hairdresser.getPassword());
  }

  @Test
  public void testGetAppointments() {
    String email = "test@example.com";
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail(email);
    hairdresser.setApproved(true);

    Appointment appointment1 = new Appointment();
    appointment1.setCompleted(true);
    Appointment appointment2 = new Appointment();
    appointment2.setCompleted(false);

    List<Appointment> appointments = Arrays.asList(appointment1, appointment2);
    when(hairdresserService.findUserByEmail(email)).thenReturn(Optional.of(hairdresser));
    when(appointmentService.findByHairdresser(hairdresser)).thenReturn(appointments);

    AppointmentDTO result = hairdresserServiceFacade.getAppointments(email);

    List<Appointment> expectedCompletedAppointments = Collections.singletonList(appointment1);
    List<Appointment> expectedPendingAppointments = Collections.singletonList(appointment2);
    assertEquals(expectedCompletedAppointments, result.completedAppointments());
    assertEquals(expectedPendingAppointments, result.pendingAppointments());
  }

  @Test
  public void testGetAppointments_ValidHairdresser_ReturnsAppointmentDTO() {
    String email = "test@example.com";
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail(email);
    hairdresser.setApproved(true);

    Appointment appointment1 = new Appointment();
    appointment1.setCompleted(true);
    Appointment appointment2 = new Appointment();
    appointment2.setCompleted(false);

    List<Appointment> appointments = Arrays.asList(appointment1, appointment2);
    when(hairdresserService.findUserByEmail(email)).thenReturn(Optional.of(hairdresser));
    when(appointmentService.findByHairdresser(hairdresser)).thenReturn(appointments);

    AppointmentDTO result = hairdresserServiceFacade.getAppointments(email);

    List<Appointment> expectedCompletedAppointments = Collections.singletonList(appointment1);
    List<Appointment> expectedPendingAppointments = Collections.singletonList(appointment2);
    Assertions.assertEquals(expectedCompletedAppointments, result.completedAppointments());
    Assertions.assertEquals(expectedPendingAppointments, result.pendingAppointments());
  }

  @Test
  public void testGetAppointments_InvalidHairdresser_ThrowsUserNotFoundException() {
    String email = "test@example.com";
    when(hairdresserService.findUserByEmail(email)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> hairdresserServiceFacade.getAppointments(email));
  }

  @Test
  public void testGetAppointments_UnapprovedHairdresser_ThrowsUnauthorizedActionException() {
    String email = "test@example.com";
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail(email);
    hairdresser.setApproved(false);

    when(hairdresserService.findUserByEmail(email)).thenReturn(Optional.of(hairdresser));

    assertThrows(UnauthorizedActionException.class,
        () -> hairdresserServiceFacade.getAppointments(email));
  }

  @Test
  public void testCompleteAppointment_InvalidAppointmentId_ThrowsAppointmentNotFoundException() {
    Long appointmentId = 123L;
    String email = "test@example.com";
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail(email);

    when(hairdresserService.findUserByEmail(email)).thenReturn(Optional.of(hairdresser));
    when(appointmentService.findById(appointmentId)).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class,
        () -> hairdresserServiceFacade.completeAppointment(appointmentId, email));
  }

  @Test
  public void testCompleteAppointment_UnauthorizedHairdresser_ThrowsUnauthorizedActionException() {
    Long appointmentId = 123L;
    String email = "test@example.com";
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail(email);

    Appointment appointment = new Appointment();
    appointment.setId(appointmentId);
    appointment.setHairdresser(new Hairdresser());

    when(hairdresserService.findUserByEmail(email)).thenReturn(Optional.of(hairdresser));
    when(appointmentService.findById(appointmentId)).thenReturn(Optional.of(appointment));

    assertThrows(UnauthorizedActionException.class,
        () -> hairdresserServiceFacade.completeAppointment(appointmentId, email));
  }

  @Test
  public void testRegisterHairdresser_ValidInputData_RegistersHairdresser() {
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setEmail("test@example.com");
    hairdresser.setPassword("password");
    Map<Integer, WorkingHoursDTO> workingHoursDtoMap = new HashMap<>();

    when(passwordEncoder.encode(hairdresser.getPassword())).thenReturn("encoded_password");
    when(roleService.getRoleByName("ROLE_HAIRDRESSER")).thenReturn(Optional.of(new Role()));
    when(beautyServiceService.findAllByIdIn(any())).thenReturn(new ArrayList<>());

    hairdresserServiceFacade.registerHairdresser(hairdresser, workingHoursDtoMap);

    verify(hairdresserService).saveHairdresser(hairdresser);
    assertEquals("encoded_password", hairdresser.getPassword());
  }

}