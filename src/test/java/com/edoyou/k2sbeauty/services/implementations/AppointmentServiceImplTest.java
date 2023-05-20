package com.edoyou.k2sbeauty.services.implementations;

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