package com.edoyou.k2sbeauty.services.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.services.implementations.NotificationService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class AdminServiceFacadeTest {

  @Mock
  private AppointmentService appointmentService;

  @Mock
  private HairdresserService hairdresserService;

  @Mock
  private BeautyServiceService beautyServiceService;

  @Mock
  private RoleService roleService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private WorkingHoursService workingHoursService;

  @Mock
  private EntityManager entityManager;

  private AdminServiceFacade adminServiceFacade;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    adminServiceFacade = new AdminServiceFacade(appointmentService, hairdresserService,
        beautyServiceService, roleService, notificationService, workingHoursService, entityManager);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindAllAppointments() {
    Pageable pageable = mock(Pageable.class);
    Page<Appointment> expectedAppointments = mock(Page.class);
    when(appointmentService.findAllAppointments(pageable)).thenReturn(expectedAppointments);

    Page<Appointment> actualAppointments = adminServiceFacade.findAllAppointments(pageable);

    assertEquals(expectedAppointments, actualAppointments);
    verify(appointmentService, times(1)).findAllAppointments(pageable);
  }

  @Test
  public void testChangeTimeSlot() {
    Long appointmentId = 1L;
    String newTimeSlot = "2023-05-26T13:00:00";
    Appointment appointment = new Appointment();
    appointment.setId(appointmentId);

    when(appointmentService.findById(appointmentId)).thenReturn(Optional.of(appointment));

    adminServiceFacade.changeTimeSlot(appointmentId, newTimeSlot);

    assertEquals(LocalDateTime.parse(newTimeSlot, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        appointment.getAppointmentTime());
    verify(appointmentService, times(1)).updateAppointment(appointmentId, appointment);
  }

  @Test
  public void testUpdatePaymentStatus() {
    Long appointmentId = 1L;
    Appointment appointment = new Appointment();
    appointment.setPaymentStatus(PaymentStatus.PENDING);
    appointment.setId(appointmentId);
    when(appointmentService.findById(appointmentId)).thenReturn(Optional.of(appointment));

    doAnswer(invocation -> {
      PaymentStatus arg = invocation.getArgument(1);
      appointment.setPaymentStatus(arg);
      return null;
    }).when(appointmentService).updatePaymentStatus(eq(appointmentId), any(PaymentStatus.class));

    adminServiceFacade.updatePaymentStatus(appointmentId);

    assertEquals(PaymentStatus.PAID, appointment.getPaymentStatus());
    verify(appointmentService, times(1)).updatePaymentStatus(appointmentId, PaymentStatus.PAID);
  }


  @Test
  public void testCancelAppointment() {
    Long appointmentId = 1L;
    doNothing().when(appointmentService).deleteAppointment(appointmentId, "ROLE_ADMIN");

    adminServiceFacade.cancelAppointment(appointmentId);

    verify(appointmentService, times(1)).deleteAppointment(appointmentId, "ROLE_ADMIN");
  }

  @Test
  public void testApproveHairdresser() {
    Long hairdresserId = 1L;
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setId(hairdresserId);
    hairdresser.setApproved(false);
    Role roleHairdresser = new Role();
    roleHairdresser.setName("ROLE_HAIRDRESSER");
    when(hairdresserService.findById(hairdresserId)).thenReturn(hairdresser);
    when(roleService.getRoleByName("ROLE_HAIRDRESSER")).thenReturn(Optional.of(roleHairdresser));

    adminServiceFacade.approveHairdresser(hairdresserId);

    assertTrue(hairdresser.isApproved());
    assertTrue(hairdresser.getRoles().contains(roleHairdresser));
    verify(hairdresserService, times(1)).saveHairdresser(hairdresser);
    verify(notificationService, times(1)).sendNotification(any(), any(), any());
  }

  @Test
  public void testRejectHairdresser() {
    Long hairdresserId = 1L;
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setId(hairdresserId);
    hairdresser.setApproved(false);
    Set<WorkingHours> workingHoursSet = new HashSet<>();
    hairdresser.setWorkingHours(workingHoursSet);
    when(hairdresserService.findById(hairdresserId)).thenReturn(hairdresser);

    adminServiceFacade.rejectHairdresser(hairdresserId);

    assertFalse(hairdresser.isApproved());
    verify(hairdresserService, times(1)).deleteHairdresser(hairdresserId);
    verify(notificationService, times(1)).sendNotification(any(), any(), any());
  }

  @Test
  public void testAssignServiceToHairdresser() {
    Long hairdresserId = 1L;
    Long serviceId = 1L;
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setId(hairdresserId);
    BeautyService beautyService = new BeautyService();
    beautyService.setId(serviceId);
    when(hairdresserService.findById(hairdresserId)).thenReturn(hairdresser);
    when(beautyServiceService.findById(serviceId)).thenReturn(Optional.of(beautyService));

    adminServiceFacade.assignServiceToHairdresser(hairdresserId, serviceId);

    assertTrue(hairdresser.getBeautyServices().contains(beautyService));
    verify(hairdresserService, times(1)).saveHairdresser(hairdresser);
  }

  @Test
  public void testSaveService() {
    BeautyService beautyService = new BeautyService();
    doNothing().when(beautyServiceService).saveService(beautyService);

    adminServiceFacade.saveService(beautyService);

    verify(beautyServiceService, times(1)).saveService(beautyService);
  }

  @Test
  public void testFindAllHairdressers() {
    List<Hairdresser> expectedHairdressers = List.of(mock(Hairdresser.class));
    when(hairdresserService.findAllHairdressers()).thenReturn(expectedHairdressers);

    List<Hairdresser> actualHairdressers = adminServiceFacade.findAllHairdressers();

    assertEquals(expectedHairdressers, actualHairdressers);
    verify(hairdresserService, times(1)).findAllHairdressers();
  }

  @Test
  public void testFindAllBeautyServices() {
    List<BeautyService> expectedBeautyServices = List.of(mock(BeautyService.class));
    when(beautyServiceService.findAllServices()).thenReturn(expectedBeautyServices);

    List<BeautyService> actualBeautyServices = adminServiceFacade.findAllBeautyServices();

    assertEquals(expectedBeautyServices, actualBeautyServices);
    verify(beautyServiceService, times(1)).findAllServices();
  }

  @Test
  public void testFindAllYetNotApproved() {
    Pageable pageable = mock(Pageable.class);
    Page<Hairdresser> expectedHairdressers = new PageImpl<>(List.of(new Hairdresser()));
    when(hairdresserService.findAllHairdressersByApprovalStatus(false, pageable)).thenReturn(
        expectedHairdressers);

    Page<Hairdresser> actualHairdressers = adminServiceFacade.findAllYetNotApproved(false,
        pageable);

    assertEquals(expectedHairdressers, actualHairdressers);
    verify(hairdresserService, times(1)).findAllHairdressersByApprovalStatus(false, pageable);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindHairdressersWithServices() {
    Pageable pageable = mock(Pageable.class);
    Page<Hairdresser> expectedHairdressers = mock(Page.class);

    when(hairdresserService.findAllWithBeautyServices(pageable)).thenReturn(expectedHairdressers);

    Page<Hairdresser> actualHairdressers = adminServiceFacade.findHairdressersWithServices(
        pageable);

    assertEquals(expectedHairdressers, actualHairdressers);
    verify(hairdresserService, times(1)).findAllWithBeautyServices(pageable);
  }

  @Test
  public void testDeleteWorkingHours() {
    Set<WorkingHours> workingHoursSet = new HashSet<>();
    WorkingHours workingHours = mock(WorkingHours.class);
    workingHoursSet.add(workingHours);

    when(workingHours.getHairdressers()).thenReturn(Collections.emptySet());

    adminServiceFacade.deleteWorkingHours(workingHoursSet);

    verify(entityManager, times(1)).refresh(workingHours);
    verify(workingHoursService, times(1)).deleteWorkingHours(workingHours.getId());
  }

  @Test
  public void testNotifyHairdresser_Approved() {
    Hairdresser hairdresser = mock(Hairdresser.class);
    when(hairdresser.isApproved()).thenReturn(true);
    when(hairdresser.getFirstName()).thenReturn("Test");
    when(hairdresser.getEmail()).thenReturn("test@test.com");

    adminServiceFacade.notifyHairdresser(hairdresser);

    verify(notificationService, times(1)).sendNotification(anyString(), eq("Application Approved"),
        anyString());
  }

  @Test
  public void testNotifyHairdresser_Rejected() {
    Hairdresser hairdresser = mock(Hairdresser.class);
    when(hairdresser.isApproved()).thenReturn(false);
    when(hairdresser.getFirstName()).thenReturn("Test");
    when(hairdresser.getEmail()).thenReturn("test@test.com");

    adminServiceFacade.notifyHairdresser(hairdresser);

    verify(notificationService, times(1)).sendNotification(anyString(), eq("Application Rejected"),
        anyString());
  }

}