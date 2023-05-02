package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AppointmentService interface represents the service layer for Appointment related operations.
 *
 * @see Appointment
 */
public interface AppointmentService {

  /**
   * Save a new appointment to the database.
   *
   * @param appointment Appointment to be saved.
   * @return The saved Appointment object.
   */
  Appointment saveAppointment(Appointment appointment);

  /**
   * Delete an appointment from the database by ID.
   *
   * @param id ID of the appointment to be deleted.
   */
  void deleteAppointment(Long id);

  /**
   * Find all appointments for a specific client.
   *
   * @param client The client to search for.
   * @return A list of appointments for the given client.
   */
  List<Appointment> findByClient(Client client);

  /**
   * Find all appointments with a specific hairdresser.
   *
   * @param hairdresser The hairdresser to search for.
   * @return A list of appointments with the given hairdresser.
   */
  List<Appointment> findByHairdresser(Hairdresser hairdresser);

  /**
   * Find all appointments for a specific beautyService.
   *
   * @param beautyService The beautyService to search for.
   * @return A list of appointments for the given beautyService.
   */
  List<Appointment> findByBeautyService(BeautyService beautyService);

  /**
   * Find all appointments between a specific time range.
   *
   * @param start The start of the time range.
   * @param end   The end of the time range.
   * @return A list of appointments within the given time range.
   */
  List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

  /**
   * Find all appointments for a specific client and hairdresser.
   *
   * @param client      The client to search for.
   * @param hairdresser The hairdresser to search for.
   * @return A list of appointments for the given client and hairdresser.
   */
  List<Appointment> findByClientAndHairdresser(Client client, Hairdresser hairdresser);
}