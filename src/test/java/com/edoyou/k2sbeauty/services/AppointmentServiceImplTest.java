package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.*;
import com.edoyou.k2sbeauty.repositories.AppointmentRepository;
import com.edoyou.k2sbeauty.services.implementations.AppointmentServiceImpl;
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
  private BeautyService beautyService;

  @BeforeEach
  public void setUp() {
    client = new Client();
    hairdresser = new Hairdresser();
    beautyService = new BeautyService();
    appointment = new Appointment();

    LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).plusMinutes(1);
    appointment.setClient(client);
    appointment.setHairdresser(hairdresser);
    appointment.setBeautyService(beautyService);
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
  @DisplayName("Save appointment with hairdresser unavailable")
  public void saveAppointment_hairdresserUnavailable() {
    List<Appointment> hairdresserAppointments = Collections.singletonList(appointment);
    when(appointmentRepository.findByHairdresser(hairdresser)).thenReturn(hairdresserAppointments);

    Appointment newAppointment = new Appointment();
    newAppointment.setHairdresser(hairdresser);
    newAppointment.setAppointmentTime(appointment.getAppointmentTime());

    assertThatThrownBy(() -> appointmentService.saveAppointment(newAppointment))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Hairdresser is not available during the appointment time.");
  }

  @Test
  @DisplayName("Save appointment with client unavailable")
  public void saveAppointment_clientUnavailable() {
    List<Appointment> clientAppointments = Collections.singletonList(appointment);
    when(appointmentRepository.findByClient(client)).thenReturn(clientAppointments);

    Appointment newAppointment = new Appointment();
    newAppointment.setClient(client);
    newAppointment.setAppointmentTime(appointment.getAppointmentTime());

    assertThatThrownBy(() -> appointmentService.saveAppointment(newAppointment))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("You already have an appointment scheduled at the same time.");
  }

  @Test
  @DisplayName("Delete existing appointment")
  public void deleteAppointment_existing() {
    long appointmentId = 1L;
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

    appointmentService.deleteAppointment(appointmentId);

    verify(appointmentRepository, times(1)).deleteById(appointmentId);
  }

  @Test
  @DisplayName("Delete non-existing appointment")
  public void deleteAppointment_nonExisting() {
    long appointmentId = 1L;
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> appointmentService.deleteAppointment(appointmentId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Appointment with id " + appointmentId + " does not exist.");
  }

  @Test
  @DisplayName("Delete appointment within 24 hours of appointment time")
  public void deleteAppointment_within24Hours() {
    long appointmentId = 1L;
    LocalDateTime appointmentTime = LocalDateTime.now().plusHours(23);
    appointment.setAppointmentTime(appointmentTime);
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

    assertThatThrownBy(() -> appointmentService.deleteAppointment(appointmentId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot cancel appointments within 24 hours of the appointment time.");
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
  @DisplayName("Find appointments by service")
  public void findByService() {
    List<Appointment> appointments = Collections.singletonList(appointment);
    when(appointmentRepository.findByBeautyService(beautyService)).thenReturn(appointments);

    List<Appointment> foundAppointments = appointmentService.findByBeautyService(beautyService);

    assertThat(foundAppointments).isEqualTo(appointments);
    verify(appointmentRepository, times(1)).findByBeautyService(beautyService);
  }

  @Test
  @DisplayName("Find appointments by appointment time between two date times")
  public void findByAppointmentTimeBetween() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);
    List<Appointment> appointments = Collections.singletonList(appointment);
    when(appointmentRepository.findByAppointmentTimeBetween(startTime, endTime)).thenReturn(
        appointments);

    List<Appointment> foundAppointments = appointmentService.findByAppointmentTimeBetween(startTime,
        endTime);

    assertThat(foundAppointments).isEqualTo(appointments);
    verify(appointmentRepository, times(1)).findByAppointmentTimeBetween(startTime, endTime);
  }

  @Test
  @DisplayName("Find appointments by client and hairdresser")
  public void findByClientAndHairdresser() {
    List<Appointment> appointments = Collections.singletonList(appointment);
    when(appointmentRepository.findByClientAndHairdresser(client, hairdresser)).thenReturn(
        appointments);

    List<Appointment> foundAppointments = appointmentService.findByClientAndHairdresser(client,
        hairdresser);

    assertThat(foundAppointments).isEqualTo(appointments);
    verify(appointmentRepository, times(1)).findByClientAndHairdresser(client, hairdresser);
  }

  @Test
  @DisplayName("Find all appointments")
  public void findAllAppointments() {
    List<Appointment> appointments = Collections.singletonList(appointment);
    when(appointmentRepository.findAll()).thenReturn(appointments);

    List<Appointment> foundAppointments = appointmentService.findAllAppointments();

    assertThat(foundAppointments).isEqualTo(appointments);
    verify(appointmentRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Update existing appointment")
  public void updateAppointment_existing() {
    Long appointmentId = 1L;
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

    Appointment updatedAppointmentDetails = new Appointment();
    updatedAppointmentDetails.setClient(new Client());
    updatedAppointmentDetails.setHairdresser(new Hairdresser());
    updatedAppointmentDetails.setBeautyService(new BeautyService());
    updatedAppointmentDetails.setAppointmentTime(LocalDateTime.now().plusDays(2));

    Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, updatedAppointmentDetails);

    assertThat(updatedAppointment).isEqualTo(appointment);
    verify(appointmentRepository, times(1)).findById(appointmentId);
    verify(appointmentRepository, times(1)).save(appointment);
  }

  @Test
  @DisplayName("Update non-existing appointment")
  public void updateAppointment_nonExisting() {
    Long appointmentId = 1L;
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

    Appointment updatedAppointmentDetails = new Appointment();

    assertThatThrownBy(() -> appointmentService.updateAppointment(appointmentId, updatedAppointmentDetails))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Appointment with id " + appointmentId + " does not exist.");
  }
}