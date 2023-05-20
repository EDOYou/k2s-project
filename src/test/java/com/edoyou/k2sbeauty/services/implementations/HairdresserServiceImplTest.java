package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.services.implementations.appointment_details.TimeSlotService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HairdresserServiceImplTest {

  @Mock
  private HairdresserRepository hairdresserRepository;

  @Mock
  private AppointmentService appointmentService;

  @Mock
  private TimeSlotService timeSlotService;

  @Mock
  private FeedbackService feedbackService;

  @InjectMocks
  private HairdresserServiceImpl hairdresserService;

  private Hairdresser hairdresser;
  private Appointment appointment;

  @BeforeEach
  void setUp() {
    hairdresser = new Hairdresser();
    hairdresser.setId(1L);
    hairdresser.setFirstName("Kanan");
    hairdresser.setLastName("Taghiyev");
    hairdresser.setEmail("taghiyev@gmail.com");
    hairdresser.setPassword("123");
    hairdresser.setPhone("12345");
    hairdresser.setSpecialization("Colorist");

    appointment = new Appointment();
    appointment.setId(2L);
    appointment.setHairdresser(hairdresser);
    hairdresser.setAppointments(new HashSet<>(Collections.singletonList(appointment)));
  }

  @Test
  void shouldSaveHairdresser() {
    when(hairdresserRepository.save(any(Hairdresser.class))).thenReturn(hairdresser);

    hairdresserService.saveHairdresser(hairdresser);

    verify(hairdresserRepository, times(1)).save(any(Hairdresser.class));
  }

  @Test
  void shouldDeleteHairdresser() {
    when(hairdresserRepository.findById(hairdresser.getId())).thenReturn(Optional.of(hairdresser));
    doNothing().when(hairdresserRepository).delete(hairdresser);

    hairdresserService.deleteHairdresser(hairdresser.getId());

    verify(hairdresserRepository, times(1)).delete(hairdresser);
  }

  @Test
  void shouldThrowExceptionWhenDeleteNonExistentHairdresser() {
    when(hairdresserRepository.findById(anyLong())).thenReturn(Optional.empty());
    Exception exception = assertThrows(ResourceNotFoundException.class,
        () -> hairdresserService.deleteHairdresser(1000L));
    String expectedMessage = "Hairdresser not found with ID: 1000";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void shouldFindHairdresserById() {
    when(hairdresserRepository.findById(anyLong())).thenReturn(Optional.of(hairdresser));

    Hairdresser foundHairdresser = hairdresserService.findById(hairdresser.getId());

    verify(hairdresserRepository, times(1)).findById(anyLong());
    assertEquals(hairdresser.getId(), foundHairdresser.getId());
    assertEquals(hairdresser.getFirstName(), foundHairdresser.getFirstName());
    assertEquals(hairdresser.getLastName(), foundHairdresser.getLastName());
  }

  @Test
  void shouldThrowExceptionWhenSortByIsInvalid() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> hairdresserService.findAllHairdressers("invalid"));
    String expectedMessage = "Invalid sortBy value: invalid";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void shouldThrowExceptionWhenSortByIsInvalidWithNullServiceId() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> hairdresserService.findAllHairdressersByServiceId("invalid", null));
    String expectedMessage = "Invalid sortBy value: invalid";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void shouldThrowExceptionWhenSortByIsInvalidInCreateSortBy() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> hairdresserService.createSortBy("invalid"));
    String expectedMessage = "Invalid sortBy value: invalid";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void shouldThrowExceptionWhenHairdresserNotFound() {
    when(hairdresserRepository.findById(anyLong())).thenReturn(Optional.empty());

    Exception exception = assertThrows(ResourceNotFoundException.class,
        () -> hairdresserService.findById(hairdresser.getId()));

    String expectedMessage = "Hairdresser with id " + hairdresser.getId() + " not found";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testGenerateSchedule() {
    when(timeSlotService.generateTimeSlots(hairdresser)).thenReturn(Collections.emptyList());
    when(appointmentService.findByHairdresser(hairdresser)).thenReturn(
        Collections.singletonList(appointment));

    hairdresserService.generateSchedule(hairdresser);

    verify(timeSlotService).generateTimeSlots(hairdresser);
    verify(appointmentService).findByHairdresser(hairdresser);
  }

  @Test
  public void testUpdateRating() {
    Feedback feedback = new Feedback();
    feedback.setRating(5);

    when(hairdresserRepository.findByIdWithAppointments(hairdresser.getId())).thenReturn(
        java.util.Optional.of(hairdresser));
    when(feedbackService.getFeedbackById(appointment.getId())).thenReturn(
        java.util.Optional.of(feedback));

    hairdresserService.updateRating(hairdresser);

    verify(hairdresserRepository).findByIdWithAppointments(hairdresser.getId());
    verify(feedbackService).getFeedbackById(appointment.getId());
    verify(hairdresserRepository).save(any(Hairdresser.class));
  }

  @Test
  void testFindAllHairdressers() {
    List<Hairdresser> hairdressers = new ArrayList<>();
    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findAll()).thenReturn(hairdressers);

    List<Hairdresser> result = hairdresserService.findAllHairdressers();

    assertEquals(2, result.size());
  }

  @Test
  void testFindAllHairdressersSortBy() {
    List<Hairdresser> hairdressers = new ArrayList<>();
    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findByIsApprovedTrue(any())).thenReturn(hairdressers);

    List<Hairdresser> result = hairdresserService.findAllHairdressers("lastName");

    assertEquals(2, result.size());
  }

  @Test
  void testFindAllHairdressersByServiceId() {
    List<Hairdresser> hairdressers = new ArrayList<>();
    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findAllByService(any(), any())).thenReturn(hairdressers);

    List<Hairdresser> result = hairdresserService.findAllHairdressersByServiceId("serviceName", 1L);

    assertEquals(2, result.size());
  }

  @Test
  public void testFindAllHairdressersByServiceId_NullServiceId() {
    hairdresserService.findAllHairdressersByServiceId("serviceName", null);
  }

  @Test
  public void testFindAllHairdressersByServiceId_ServiceIdWithNoHairdressers() {
    when(hairdresserRepository.findAllByService(anyLong(), any(Sort.class))).thenReturn(
        new ArrayList<>());

    Long serviceId = 1L;
    List<Hairdresser> result = hairdresserService.findAllHairdressersByServiceId("serviceName",
        serviceId);

    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindAllHairdressersByServiceId_NonExistentServiceId() {
    when(hairdresserRepository.findAllByService(anyLong(), any(Sort.class))).thenReturn(
        new ArrayList<>());

    Long serviceId = -1L;
    List<Hairdresser> result = hairdresserService.findAllHairdressersByServiceId("serviceName",
        serviceId);

    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindAllHairdressersByServiceId_NullSortBy() {
    Long serviceId = 1L;
    hairdresserService.findAllHairdressersByServiceId(null, serviceId);
    verify(hairdresserRepository).findAllByService(eq(serviceId), any(Sort.class));
  }


  @Test
  void testFindAllHairdressersByApprovalStatus() {
    Page<Hairdresser> page = Mockito.mock(Page.class);
    when(hairdresserRepository.findByIsApproved(anyBoolean(), any())).thenReturn(page);

    Pageable pageable = Mockito.mock(Pageable.class);
    Page<Hairdresser> result = hairdresserService.findAllHairdressersByApprovalStatus(true,
        pageable);

    assertEquals(page, result);
  }

  @Test
  void testFindAllWithBeautyServices() {
    List<Hairdresser> hairdressers = new ArrayList<>();
    hairdressers.add(new Hairdresser());
    hairdressers.add(new Hairdresser());

    when(hairdresserRepository.findAllWithBeautyServices()).thenReturn(hairdressers);

    List<Hairdresser> result = hairdresserService.findAllWithBeautyServices();

    assertEquals(2, result.size());
  }

  @Test
  void testFindAllWithBeautyServicesWithPageable() {
    Page<Hairdresser> hairdresserPage = mock(Page.class);

    when(hairdresserRepository.findAllWithBeautyServices(any(Pageable.class))).thenReturn(
        hairdresserPage);

    Page<Hairdresser> result = hairdresserService.findAllWithBeautyServices(Pageable.unpaged());

    assertEquals(hairdresserPage, result);
  }

}