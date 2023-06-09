package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;

import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Delete an appointment from the database by ID. 24-hour check is skipped for ROLE-ADMIN.
     *
     * @param id ID of the appointment to be deleted.
     */
    void deleteAppointment(Long id, String userRole);

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
     * Find all appointments.
     *
     * @return A list of all appointments.
     */
    List<Appointment> findAllAppointments();

    /**
     * Update an existing appointment.
     *
     * @param id                 The ID of the appointment to update.
     * @param appointmentDetails The updated appointment details.
     * @return The updated Appointment object.
     */
    Appointment updateAppointment(Long id, Appointment appointmentDetails);

    /**
     * Retrieves a list of all appointments.
     *
     * @param pageable The pagination information.
     * @return A Page object containing appointments.
     */
    Page<Appointment> findAllAppointments(Pageable pageable);

    /**
     * Retrieves an appointment by ID.
     *
     * @param id The ID of the appointment to find.
     * @return An Optional containing the found appointment, or an empty Optional if not found.
     */
    Optional<Appointment> findById(Long id);

    /**
     * Updates the payment status of an appointment.
     *
     * @param appointmentId The ID of the appointment to update.
     * @param status        The new payment status.
     */
    void updatePaymentStatus(Long appointmentId, PaymentStatus status);
}