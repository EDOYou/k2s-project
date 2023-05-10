package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.implementations.AppointmentServiceImpl;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentRepositoryTest {

  @Mock
  private AppointmentRepository appointmentRepository;

  @InjectMocks
  private AppointmentServiceImpl appointmentService;

  private Client client;
  private Hairdresser hairdresser;
  private BeautyService beautyService;
  private Appointment appointment;
  private LocalDateTime appointmentTime;

  @BeforeEach
  void setUp() {
    client = new Client();
    hairdresser = new Hairdresser();
    beautyService = new BeautyService();
    appointment = new Appointment();
    appointment.setId(1L);
    appointment.setClient(client);
    appointment.setHairdresser(hairdresser);
    appointment.setBeautyService(beautyService);
    appointment.setAppointmentTime(LocalDateTime.now().plusDays(2));

    appointmentTime = LocalDateTime.now().plusDays(2);
    appointment.setAppointmentTime(appointmentTime);
  }

  @Test
  void findByClientTest() {
    when(appointmentRepository.findByClient(client)).thenReturn(Arrays.asList(appointment));
    List<Appointment> appointments = appointmentService.findByClient(client);
    assertEquals(1, appointments.size());
    assertEquals(client, appointments.get(0).getClient());
  }

  @Test
  void findByHairdresserTest() {
    when(appointmentRepository.findByHairdresser(hairdresser)).thenReturn(Arrays.asList(appointment));
    List<Appointment> appointments = appointmentService.findByHairdresser(hairdresser);
    assertEquals(1, appointments.size());
    assertEquals(hairdresser, appointments.get(0).getHairdresser());
  }

  @Test
  void saveAppointmentTest() {
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
    Appointment savedAppointment = appointmentService.saveAppointment(appointment);
    assertEquals(client, savedAppointment.getClient());
    assertEquals(hairdresser, savedAppointment.getHairdresser());
    assertEquals(beautyService, savedAppointment.getBeautyService());
    assertEquals(appointmentTime, savedAppointment.getAppointmentTime());
  }

  @Test
  public void saveAppointment_success() {
    when(appointmentRepository.findByClient(client)).thenReturn(Collections.emptyList());
    when(appointmentRepository.findByHairdresser(hairdresser)).thenReturn(Collections.emptyList());
    when(appointmentRepository.save(appointment)).thenReturn(appointment);

    Appointment savedAppointment = appointmentService.saveAppointment(appointment);

    assertNotNull(savedAppointment);
    assertEquals(appointment.getId(), savedAppointment.getId());
  }

  @Test
  public void saveAppointment_hairdresserNotAvailable() {
    when(appointmentRepository.findByHairdresser(hairdresser)).thenReturn(List.of(appointment));

    assertThrows(IllegalStateException.class, () -> appointmentService.saveAppointment(appointment));
  }

  @Test
  public void saveAppointment_clientNotAvailable() {
    when(appointmentRepository.findByClient(client)).thenReturn(List.of(appointment));

    assertThrows(IllegalStateException.class, () -> appointmentService.saveAppointment(appointment));
  }

  @Test
  void findByIdTest() {
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
    Optional<Appointment> foundAppointment = appointmentService.findById(1L);
    assertEquals(client, foundAppointment.get().getClient());
    assertEquals(hairdresser, foundAppointment.get().getHairdresser());
    assertEquals(beautyService, foundAppointment.get().getBeautyService());
    assertEquals(appointmentTime, foundAppointment.get().getAppointmentTime());
  }
}