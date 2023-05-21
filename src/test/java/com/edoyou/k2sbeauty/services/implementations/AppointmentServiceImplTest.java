package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.*;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.AppointmentRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceImplTest {

  @Mock
  private AppointmentRepository appointmentRepository;

  @InjectMocks
  private AppointmentServiceImpl appointmentService;

  private Appointment appointment;
  private Client client;
  private Hairdresser hairdresser;

  @BeforeEach
  public void setUp() {
    client = new Client();
    hairdresser = new Hairdresser();
    appointment = new Appointment();

    LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).plusMinutes(1);
    appointment.setClient(client);
    appointment.setHairdresser(hairdresser);
    appointment.setAppointmentTime(appointmentTime);
  }

  @Test
  @DisplayName("Save appointment with valid data")
  public void saveAppointment_valid() {
    when(appointmentRepository.findByClient(any(Client.class))).thenReturn(new ArrayList<>());
    when(appointmentRepository.findByHairdresser(any(Hairdresser.class))).thenReturn(
        new ArrayList<>());
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

    Appointment savedAppointment = appointmentService.saveAppointment(appointment);

    assertThat(savedAppointment).isEqualTo(appointment);
    verify(appointmentRepository, times(1)).save(appointment);
  }

  @Test
  @DisplayName("Save appointment with past appointment time")
  public void saveAppointment_pastAppointmentTime() {
    LocalDateTime pastAppointmentTime = LocalDateTime.now().minusDays(1);
    appointment.setAppointmentTime(pastAppointmentTime);

    assertThatThrownBy(() -> appointmentService.saveAppointment(appointment))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Appointment time must be in the future.");
  }

  @Test
  @DisplayName("Save appointment when client already has an appointment at the same time")
  public void saveAppointment_clientUnavailable() {
    Client client = new Client();
    client.setId(1L);

    LocalDateTime existingAppointmentTime = LocalDateTime.now().plusDays(1);
    Appointment existingAppointment = new Appointment();
    existingAppointment.setId(1L);
    existingAppointment.setCompleted(false);
    existingAppointment.setAppointmentTime(existingAppointmentTime);
    existingAppointment.setClient(client);

    Appointment newAppointment = new Appointment();
    newAppointment.setId(2L);
    newAppointment.setAppointmentTime(existingAppointmentTime);
    newAppointment.setClient(client);

    List<Appointment> existingAppointments = new ArrayList<>();
    existingAppointments.add(existingAppointment);

    when(appointmentRepository.findByClient(any(Client.class))).thenReturn(existingAppointments);

    assertThatThrownBy(() -> appointmentService.saveAppointment(newAppointment))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("You already have an appointment scheduled at the same time.");
  }

  @Test
  @DisplayName("Save appointment when hairdresser already has an appointment at the same time")
  public void saveAppointment_hairdresserUnavailable() {
    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setId(1L);

    LocalDateTime existingAppointmentTime = LocalDateTime.now().plusDays(1);
    Appointment existingAppointment = new Appointment();
    existingAppointment.setId(1L);
    existingAppointment.setCompleted(false);
    existingAppointment.setAppointmentTime(existingAppointmentTime);
    existingAppointment.setHairdresser(hairdresser);

    Appointment newAppointment = new Appointment();
    newAppointment.setId(2L);
    newAppointment.setAppointmentTime(existingAppointmentTime);
    newAppointment.setHairdresser(hairdresser);

    List<Appointment> existingAppointments = new ArrayList<>();
    existingAppointments.add(existingAppointment);

    when(appointmentRepository.findByHairdresser(eq(hairdresser))).thenReturn(existingAppointments);

    assertThatThrownBy(() -> appointmentService.saveAppointment(newAppointment))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Hairdresser is not available during the appointment time.");
  }

  @Test
  @DisplayName("Find all appointments handling exception")
  public void findAllAppointments_exception() {
    when(appointmentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

    List<Appointment> foundAppointments = appointmentService.findAllAppointments();

    assertThat(foundAppointments).isEmpty();
  }

  @Test
  @DisplayName("Find appointments by client")
  public void findByClient() {
    List<Appointment> appointments = Collections.singletonList(appointment);
    when(appointmentRepository.findByClient(client)).thenReturn(appointments);

    List<Appointment> foundAppointments = appointmentService.findByClient(client);

    assertThat(foundAppointments).isEqualTo(appointments);
    verify(appointmentRepository, times(1)).findByClient(client);
  }

  @Test
  @DisplayName("Find appointments by hairdresser")
  public void findByHairdresser() {
    List<Appointment> appointments = Collections.singletonList(appointment);
    when(appointmentRepository.findByHairdresser(hairdresser)).thenReturn(appointments);

    List<Appointment> foundAppointments = appointmentService.findByHairdresser(hairdresser);

    assertThat(foundAppointments).isEqualTo(appointments);
    verify(appointmentRepository, times(1)).findByHairdresser(hairdresser);
  }

  @Test
  @DisplayName("Update payment status for non-existing appointment")
  public void updatePaymentStatus_nonExistingAppointment() {
    Long appointmentId = 1L;

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> appointmentService.updatePaymentStatus(appointmentId, PaymentStatus.PAID))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Appointment not found for this id :: " + appointmentId);
  }

  @Test
  @DisplayName("Update payment status for existing appointment")
  public void updatePaymentStatus_existingAppointment() {
    Long appointmentId = 1L;
    appointment.setPaymentStatus(PaymentStatus.PENDING);

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

    appointmentService.updatePaymentStatus(appointmentId, PaymentStatus.PAID);

    assertThat(appointment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
    verify(appointmentRepository, times(1)).save(appointment);
  }

  @Test
  @DisplayName("Delete appointment within 24 hours by non-admin user")
  public void deleteAppointment_within24Hours() {
    Long appointmentId = 1L;
    appointment.setAppointmentTime(LocalDateTime.now().plusHours(23));

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

    assertThatThrownBy(() -> appointmentService.deleteAppointment(appointmentId, "ROLE_CLIENT"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(
            "Cannot cancel appointments within 24 hours of the appointment time.");
  }

  @Test
  @DisplayName("Delete non-existing appointment by admin")
  public void deleteAppointment_nonExistingAppointment() {
    Long appointmentId = 1L;

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> appointmentService.deleteAppointment(appointmentId, "ROLE_ADMIN"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Appointment with id " + appointmentId + " does not exist.");
  }

  @Test
  @DisplayName("Delete existing appointment by admin")
  public void deleteAppointment_existingAppointmentByAdmin() {
    Long appointmentId = 1L;

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

    appointmentService.deleteAppointment(appointmentId, "ROLE_ADMIN");

    verify(appointmentRepository, times(1)).deleteById(appointmentId);
  }

  @Test
  @DisplayName("Delete existing appointment by non-admin more than 24 hours in advance")
  public void deleteAppointment_existingAppointmentByNonAdmin() {
    Long appointmentId = 1L;
    appointment.setAppointmentTime(LocalDateTime.now().plusDays(1).plusHours(1));

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

    appointmentService.deleteAppointment(appointmentId, "ROLE_USER");

    verify(appointmentRepository, times(1)).deleteById(appointmentId);
  }

  @Test
  @DisplayName("Find existing appointment by id")
  public void findById_existingAppointment() {
    Long appointmentId = 1L;

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

    Optional<Appointment> foundAppointment = appointmentService.findById(appointmentId);

    assertThat(foundAppointment.isPresent()).isTrue();
    assertThat(foundAppointment.get()).isEqualTo(appointment);
  }

  @Test
  @DisplayName("Find non-existing appointment by id")
  public void findById_nonExistingAppointment() {
    Long appointmentId = 1L;

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

    Optional<Appointment> foundAppointment = appointmentService.findById(appointmentId);

    assertThat(foundAppointment.isPresent()).isFalse();
  }

  @Test
  @DisplayName("Update existing appointment")
  public void updateAppointment_existingAppointment() {
    Long appointmentId = 1L;
    Appointment newAppointmentDetails = new Appointment();
    newAppointmentDetails.setAppointmentTime(LocalDateTime.now().plusDays(2));

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

    Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId,
        newAppointmentDetails);

    assertThat(updatedAppointment.getAppointmentTime()).isEqualTo(
        newAppointmentDetails.getAppointmentTime());
  }

  @Test
  @DisplayName("Update non-existing appointment")
  public void updateAppointment_nonExistingAppointment() {
    Long appointmentId = 1L;
    Appointment newAppointmentDetails = new Appointment();

    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> appointmentService.updateAppointment(appointmentId, newAppointmentDetails))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Appointment with id " + appointmentId + " does not exist.");
  }

  @Test
  @DisplayName("Find all appointments with non-empty database")
  public void findAllAppointments_nonEmptyDatabase() {
    List<Appointment> appointments = Collections.singletonList(appointment);
    when(appointmentRepository.findAll()).thenReturn(appointments);

    List<Appointment> foundAppointments = appointmentService.findAllAppointments();

    assertThat(foundAppointments).isEqualTo(appointments);
  }

  @Test
  @DisplayName("Find all appointments with paging with non-empty database")
  public void findAllAppointmentsWithPaging_nonEmptyDatabase() {
    Pageable pageable = PageRequest.of(0, 5);
    List<Appointment> appointments = Collections.singletonList(appointment);
    Page<Appointment> page = new PageImpl<>(appointments);
    when(appointmentRepository.findAll(pageable)).thenReturn(page);

    Page<Appointment> foundAppointments = appointmentService.findAllAppointments(pageable);

    assertThat(foundAppointments.getContent()).isEqualTo(appointments);
  }

  @Test
  @DisplayName("Find all appointments with empty database")
  public void findAllAppointments_emptyDatabase() {
    when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());

    List<Appointment> foundAppointments = appointmentService.findAllAppointments();

    assertThat(foundAppointments).isEmpty();
  }

  @Test
  @DisplayName("Find all appointments with paging with empty database")
  public void findAllAppointmentsWithPaging_emptyDatabase() {
    Pageable pageable = PageRequest.of(0, 5);
    when(appointmentRepository.findAll(pageable)).thenReturn(Page.empty());

    Page<Appointment> foundAppointments = appointmentService.findAllAppointments(pageable);

    assertThat(foundAppointments.isEmpty()).isTrue();
  }

}