package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.AppointmentRepository;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * The {@code AppointmentServiceImpl} class provides an implementation of the {@code AppointmentService} interface.
 * It is responsible for the manipulation and business logic tied to the {@code Appointment} entities.
 *
 * <p> This service implementation provides methods for saving an appointment, deleting an appointment,
 * fetching appointments by client and hairdresser, fetching all appointments, fetching a single appointment by its ID,
 * updating an appointment, and updating the payment status of an appointment.
 *
 * <p> The class uses Spring's {@code @Service} annotation to indicate it's a service provider
 * and should be automatically detected by Spring for dependency injection.
 *
 * <p> An instance of {@code AppointmentRepository} is autowired to facilitate operations with the database.
 *
 * <p> Example usage:
 * <pre>{@code
 * @Autowired
 * AppointmentService appointmentService;
 *
 * public void createAppointment() {
 *   Appointment newAppointment = new Appointment();
 *   newAppointment.setClient(client);
 *   newAppointment.setHairdresser(hairdresser);
 *   appointmentService.saveAppointment(newAppointment);
 * }
 * }</pre>
 *
 * @since 2023-05-28
 * @author Taghiyev Kanan
 * @see Appointment
 * @see AppointmentService
 * @see AppointmentRepository
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger LOGGER = LogManager.getLogger(AppointmentServiceImpl.class.getName());

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method checks if the {@code appointment} time is in the future.
     * Also, it checks if the hairdresser is available and the client doesn't already
     * have another appointment at the same time before saving the appointment.
     *
     * <p>Here is the sequence of actions:
     * <ol>
     *   <li>Check if the appointment is set in the future</li>
     *   <li>Check if the hairdresser is available at the set time</li>
     *   <li>Check if the client already has an appointment at the set time</li>
     *   <li>Save the appointment in the repository</li>
     * </ol>
     * </p>
     *
     * @throws IllegalArgumentException if the appointment time is in the past
     * @throws IllegalStateException    if the hairdresser or client is not available at the set time
     */
    @Override
    public Appointment saveAppointment(Appointment appointment) {
        LOGGER.info("Saving Appointment: " + appointment);
        LocalDateTime now = LocalDateTime.now();

        if (appointment.getAppointmentTime().isBefore(now)) {
            throw new IllegalArgumentException("Appointment time must be in the future.");
        }

        List<Appointment> hairdresserAppointments = findByHairdresser(appointment.getHairdresser());
        for (Appointment existingAppointment : hairdresserAppointments) {
            if (existingAppointment.getAppointmentTime().isEqual(appointment.getAppointmentTime())
                    && !existingAppointment.getId().equals(appointment.getId())
                    && !existingAppointment.isCompleted()) {
                throw new IllegalStateException(
                        "Hairdresser is not available during the appointment time.");
            }
        }

        List<Appointment> clientAppointments = findByClient(appointment.getClient());
        for (Appointment existingAppointment : clientAppointments) {
            if (existingAppointment.getAppointmentTime().isEqual(appointment.getAppointmentTime())
                    && !existingAppointment.getId().equals(appointment.getId())
                    && !existingAppointment.isCompleted()) {
                throw new IllegalStateException(
                        "You already have an appointment scheduled at the same time.");
            }
        }
        return appointmentRepository.save(appointment);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method deletes an appointment from the database. If the user is not an admin,
     * it checks if the appointment time is more than 24 hours in the future before deleting.
     *
     * <p>Here is the sequence of actions:
     * <ol>
     *   <li>Find the appointment in the repository</li>
     *   <li>Check if the user is an admin</li>
     *   <li>If the user is not an admin, check if the appointment time is more than 24 hours in the future</li>
     *   <li>Delete the appointment from the repository</li>
     * </ol>
     * </p>
     *
     * @throws IllegalStateException if the appointment does not exist or cannot be cancelled within 24 hours
     */
    @Override
    public void deleteAppointment(Long id, String userRole) {
        LOGGER.info("Deleting the appointment...");
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
     * {@inheritDoc}
     * <p>
     * The method retrieves all appointments for the provided client from the repository,
     * sorts them in descending order based on the appointment time, and returns the sorted list.
     */
    @Override
    public List<Appointment> findByClient(Client client) {
        LOGGER.info("Find Appointments by client...");
        return appointmentRepository.findByClient(client)
                .stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * The method retrieves all appointments for the provided hairdresser from the repository,
     * sorts them in descending order based on the appointment time, and returns the sorted list.
     */
    @Override
    public List<Appointment> findByHairdresser(Hairdresser hairdresser) {
        LOGGER.info("Find Appointments by hairdresser...");
        return appointmentRepository.findByHairdresser(hairdresser)
                .stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * The method fetches all appointments from the repository.
     * In case of an error, an empty list is returned.
     */
    @Override
    public List<Appointment> findAllAppointments() {
        try {
            LOGGER.info("Appointments' size is " + appointmentRepository.findAll().size());
            List<Appointment> appointments = appointmentRepository.findAll();
            LOGGER.info("Fetched appointments: " + appointments);
            return appointments;
        } catch (Exception e) {
            LOGGER.error("Error fetching appointments: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method fetches a Page of appointments according to the supplied {@code Pageable} object,
     * which specifies pagination information such as the size of the page and the number of the page to be retrieved.
     *
     * <p>
     * Here is the sequence of actions:
     * <ol>
     *   <li>Log the current page number being fetched</li>
     *   <li>Fetch the appointments page from the repository</li>
     *   <li>Log the fetched appointments</li>
     *   <li>Return the appointments page</li>
     * </ol>
     * If an exception occurs during the fetching process, it catches the exception and returns an empty page.
     * </p>
     *
     * @param pageable Pagination information.
     * @return A {@code Page} of {@code Appointment} instances.
     */
    @Override
    public Page<Appointment> findAllAppointments(Pageable pageable) {
        try {
            LOGGER.info("Fetching appointments page: " + pageable.getPageNumber());
            Page<Appointment> appointments = appointmentRepository.findAll(pageable);
            LOGGER.info("Fetched appointments: " + appointments.getContent());
            return appointments;
        } catch (Exception e) {
            LOGGER.error("Error fetching appointments: " + e.getMessage());
            return Page.empty();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method fetches an {@code Appointment} by its ID. It simply delegates the call to the
     * underlying repository's {@code findById} method and returns the result.
     *
     * <p>
     * Here is the sequence of actions:
     * <ol>
     *   <li>Log the ID of the appointment being fetched</li>
     *   <li>Fetch the appointment from the repository</li>
     *   <li>Return the fetched appointment</li>
     * </ol>
     * </p>
     *
     * @param id The ID of the appointment to retrieve.
     * @return An {@code Optional} containing the retrieved {@code Appointment} instance if found,
     * or an empty {@code Optional} if no appointment was found with the provided ID.
     */
    @Override
    public Optional<Appointment> findById(Long id) {
        LOGGER.info("Find Appointment by ID...");
        return appointmentRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The method updates an existing appointment with new details.
     *
     * <p>Here is the sequence of actions:
     * <ol>
     *   <li>Find the existing appointment in the repository</li>
     *   <li>Update the existing appointment with new details</
     *   <li>Save the updated appointment in the repository</li>
     * </ol>
     * </p>
     *
     * @throws IllegalStateException if the appointment does not exist
     */
    @Override
    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        LOGGER.info("Updating the appointment...");
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

    /**
     * {@inheritDoc}
     * <p>
     * The method updates the payment status for an existing appointment.
     *
     * <p>Here is the sequence of actions:
     * <ol>
     *   <li>Find the existing appointment in the repository</li>
     *   <li>Update the payment status of the existing appointment</li>
     *   <li>Save the updated appointment in the repository</li>
     * </ol>
     * </p>
     *
     * @throws ResourceNotFoundException if the appointment does not exist
     */
    @Override
    public void updatePaymentStatus(Long appointmentId, PaymentStatus status) {
        LOGGER.info("Updating payment status for appointment...");
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found for this id :: " + appointmentId));
        appointment.setPaymentStatus(status);

        LOGGER.info("Updating payment status for appointment: " + appointment + " to: " + status);

        appointmentRepository.save(appointment);
    }

}