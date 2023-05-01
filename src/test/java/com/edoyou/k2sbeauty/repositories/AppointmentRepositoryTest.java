package com.edoyou.k2sbeauty.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AppointmentRepositoryTest {
  @Autowired
  private AppointmentRepository appointmentRepository;
  private Appointment appointment;

  @BeforeEach
  void setUp() {
    Client client = new Client();
    client.setFirstName("Barbara");
    client.setLastName("O'Conner");
    client.setEmail("myemail@gmail.com");
    client.setPassword("password");
    client.setPhone("36707733373");
    client.setCreatedAt(LocalDateTime.now());
    client.setUpdatedAt(LocalDateTime.now());

    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setFirstName("Jane");
    hairdresser.setLastName("Doe");
    hairdresser.setEmail("jane.doe@gmail.com");
    hairdresser.setPassword("password");
    hairdresser.setPhone("36707733444");
    hairdresser.setCreatedAt(LocalDateTime.now());
    hairdresser.setUpdatedAt(LocalDateTime.now());
    hairdresser.setSpecialization("Hairstyling");

    BeautyService beautyService = new BeautyService();
    beautyService.setName("Haircut");
    beautyService.setDescription("Sport style");
    beautyService.setPrice(25.0);

    appointment = new Appointment();
    appointment.setClient(client);
    appointment.setHairdresser(hairdresser);
    appointment.setAppointmentTime(LocalDateTime.now());
    appointment.setBeautyService(beautyService);
  }

  @Test
  public void shouldSaveAppointment() {
    Appointment savedAppointment = appointmentRepository.save(appointment);
    assertThat(savedAppointment).isNotNull();
    assertThat(savedAppointment.getId()).isNotNull();
  }

  @Test
  public void shouldFindByClient() {
    appointmentRepository.save(appointment);

    List<Appointment> foundAppointments = appointmentRepository.findByClient(appointment.getClient());
    assertThat(foundAppointments).isNotNull();
    assertThat(foundAppointments).hasSize(1);
    assertThat(foundAppointments.get(0)).isEqualTo(appointment);
  }

  @Test
  public void shouldFindByService() {
    appointmentRepository.save(appointment);

    List<Appointment> foundAppointments = appointmentRepository.findByBeautyService(appointment.getBeautyService());
    assertThat(foundAppointments).isNotNull();
    assertThat(foundAppointments).hasSize(1);
    assertThat(foundAppointments.get(0)).isEqualTo(appointment);
  }

  @Test
  public void shouldFindByAppointmentTimeBetween() {
    appointmentRepository.save(appointment);

    LocalDateTime startTime = LocalDateTime.now().minusHours(1);
    LocalDateTime endTime = LocalDateTime.now().plusHours(1);
    List<Appointment> foundAppointments = appointmentRepository.findByAppointmentTimeBetween(startTime, endTime);
    assertThat(foundAppointments).isNotNull();
    assertThat(foundAppointments).hasSize(1);
    assertThat(foundAppointments.get(0)).isEqualTo(appointment);
  }

  @Test
  public void shouldFindByClientAndHairdresser() {
    appointmentRepository.save(appointment);

    List<Appointment> foundAppointments = appointmentRepository.findByClientAndHairdresser(appointment.getClient(), appointment.getHairdresser());
    assertThat(foundAppointments).isNotNull();
    assertThat(foundAppointments).hasSize(1);
    assertThat(foundAppointments.get(0)).isEqualTo(appointment);
  }

}
