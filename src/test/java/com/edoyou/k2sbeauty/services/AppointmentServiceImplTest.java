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

  // ... other test cases for saveAppointment

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

  // ... other test cases for deleteAppointment

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
}
