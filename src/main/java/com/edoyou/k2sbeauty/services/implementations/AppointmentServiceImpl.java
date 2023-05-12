package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.AppointmentRepository;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * The {@code AppointmentServiceImpl} class implements the {@link AppointmentService} interface and
 * provides the service layer for appointment-related operations in the context of a beauty salon.
 * This implementation includes logic for saving appointments, deleting appointments, and retrieving
 * appointments based on various criteria such as client, hairdresser, and service. Additionally, it
 * ensures hairdresser and client availability and enforces a cancellation policy.
 *
 * @see Appointment
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

  private static final Logger LOGGER = Logger.getLogger(AppointmentServiceImpl.class.getName());

  private final AppointmentRepository appointmentRepository;

  @Autowired
  public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
    this.appointmentRepository = appointmentRepository;
  }

  /**
   * Saves an appointment while ensuring that the hairdresser and client are available during the
   * appointment time.
   *
   * @param appointment The appointment to be saved.
   * @return The saved appointment.
   * @throws IllegalStateException if the hairdresser or client is unavailable during the
   *                               appointment time.
   */
  @Override
  public Appointment saveAppointment(Appointment appointment) {
    LOGGER.info("Saving appointment: " + appointment);
    LocalDateTime now = LocalDateTime.now();

    if (appointment.getAppointmentTime().isBefore(now)) {
      throw new IllegalArgumentException("Appointment time must be in the future.");
    }

    List<Appointment> hairdresserAppointments = findByHairdresser(appointment.getHairdresser());
    for (Appointment existingAppointment : hairdresserAppointments) {
      if (existingAppointment.getAppointmentTime().isEqual(appointment.getAppointmentTime())) {
        throw new IllegalStateException(
            "Hairdresser is not available during the appointment time.");
      }
    }

    List<Appointment> clientAppointments = findByClient(appointment.getClient());
    for (Appointment existingAppointment : clientAppointments) {
      if (existingAppointment.getAppointmentTime().isEqual(appointment.getAppointmentTime())) {
        throw new IllegalStateException(
            "You already have an appointment scheduled at the same time.");
      }
    }
    return appointmentRepository.save(appointment);
  }

  /**
   * Deletes an appointment based on its ID, while ensuring that the appointment exists and adhering
   * to a 24-hour cancellation policy.
   *
   * @param id The ID of the appointment to be deleted.
   * @throws IllegalStateException if the appointment does not exist or if the cancellation is
   *                               within 24 hours of the appointment time.
   */
  @Override
  public void deleteAppointment(Long id, String userRole) {
    Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(
            () -> new IllegalStateException("Appointment with id " + id + " does not exist."));

    if (!"ROLE_ADMIN".equals(userRole)) {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime appointmentTime = appointment.getAppointmentTime();
      if (now.plusHours(24).isAfter(appointmentTime)) {
        throw new IllegalStateException(
            "Cannot cancel appointments within 24 hours of the appointment time.");
      }
    }

    appointmentRepository.deleteById(id);
  }

  /**
   * Retrieves appointments for a specific client, sorted by date with the most recent appointments
   * first.
   *
   * @param client The client for whom to retrieve appointments.
   * @return A list of appointments sorted by date.
   */
  @Override
  public List<Appointment> findByClient(Client client) {
    return appointmentRepository.findByClient(client)
        .stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Retrieves appointments for a specific hairdresser, sorted by date with the most recent
   * appointments first.
   *
   * @param hairdresser The hairdresser for whom to retrieve appointments.
   * @return A list of appointments sorted by date.
   */
  @Override
  public List<Appointment> findByHairdresser(Hairdresser hairdresser) {
    return appointmentRepository.findByHairdresser(hairdresser)
        .stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Retrieves appointments for a specific beauty service. The method filters out appointments that
   * are in the past, leaving only future appointments, sorted by date with the most recent
   * appointments first.
   *
   * @param beautyService The beauty service for which to retrieve appointments.
   * @return A list of appointments sorted by date.
   */
  @Override
  public List<Appointment> findByBeautyService(BeautyService beautyService) {
    if (beautyService == null) {
      throw new IllegalArgumentException("BeautyService cannot be null.");
    }

    List<Appointment> allAppointments = appointmentRepository.findByBeautyService(beautyService);
    LocalDateTime now = LocalDateTime.now();

    return allAppointments.stream()
        .filter(appointment -> appointment.getAppointmentTime().isAfter(now))
        .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Retrieves appointments within a specific time range, sorted by date with the most recent
   * appointments first.
   *
   * @param start The start of the time range for which to retrieve appointments.
   * @param end   The end of the time range for which to retrieve appointments.
   * @return A list of appointments sorted by date.
   */
  @Override
  public List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end) {
    return appointmentRepository.findByAppointmentTimeBetween(start, end)
        .stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Retrieves appointments for a specific client and hairdresser, sorted by date with the most
   * recent appointments first.
   *
   * @param client      The client for whom to retrieve appointments.
   * @param hairdresser The hairdresser for whom to retrieve appointments.
   * @return A list of appointments sorted by date.
   */
  @Override
  public List<Appointment> findByClientAndHairdresser(Client client, Hairdresser hairdresser) {
    return appointmentRepository.findByClientAndHairdresser(client, hairdresser)
        .stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
        .collect(Collectors.toList());
  }

  @Override
  public List<Appointment> findAllAppointments() {
    try {
      LOGGER.info("Appointments' size is " + appointmentRepository.findAll().size());
      List<Appointment> appointments = appointmentRepository.findAll();
      LOGGER.info("Fetched appointments: " + appointments);
      return appointments;
    } catch (Exception e) {
      LOGGER.severe("Error fetching appointments: " + e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public Optional<Appointment> findById(Long id) {
    return appointmentRepository.findById(id);
  }

  @Override
  public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
    Appointment existingAppointment = appointmentRepository.findById(id)
        .orElseThrow(
            () -> new IllegalStateException("Appointment with id " + id + " does not exist."));

    LOGGER.info("Updating appointment: " + existingAppointment + " with new details: "
        + appointmentDetails);

    existingAppointment.setClient(appointmentDetails.getClient());
    existingAppointment.setHairdresser(appointmentDetails.getHairdresser());
    existingAppointment.setBeautyService(appointmentDetails.getBeautyService());
    existingAppointment.setAppointmentTime(appointmentDetails.getAppointmentTime());

    return appointmentRepository.save(existingAppointment);
  }

  @Override
  public void updatePaymentStatus(Long appointmentId, PaymentStatus status) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Appointment not found for this id :: " + appointmentId));
    appointment.setPaymentStatus(status);

    LOGGER.info("Updating payment status for appointment: " + appointment + " to: " + status);

    appointmentRepository.save(appointment);
  }

  @Override
  public boolean isHairdresserAvailable(Long hairdresserId, Long serviceId,
      LocalDateTime appointmentDateTime) {
    List<Appointment> existingAppointments = appointmentRepository.findByHairdresserIdAndAppointmentTimeBetween(
        hairdresserId,
        appointmentDateTime.minusMinutes(1),
        appointmentDateTime.plusMinutes(1)
    );

    return existingAppointments.isEmpty();
  }
}