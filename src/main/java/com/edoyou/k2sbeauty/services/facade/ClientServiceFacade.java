package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.AppointmentNotFoundException;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.implementations.appointment_details.TimeSlotService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

/**
 * ClientServiceFacade is the central access point to all the services related to client operations in K2SBeauty.
 * It encapsulates the logic of the operations and provides a high-level API that can be used by the controllers.
 * All the operations that are related to the clients go through this class.
 * <p>
 * The class provides an interface for the client-related operations such as registration, booking an appointment,
 * providing feedback, etc.
 * <p>
 * This class is marked with {@link org.springframework.stereotype.Service} to indicate that it's a Spring
 * component and {@link org.springframework.transaction.annotation.Transactional} to handle the transactions.
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Service
public class ClientServiceFacade {

    private static final Logger LOGGER = LogManager.getLogger(ClientServiceFacade.class.getName());
    private final ClientService clientService;
    private final HairdresserService hairdresserService;
    private final BeautyServiceService beautyServiceService;
    private final AppointmentService appointmentService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TimeSlotService timeSlotService;
    private final FeedbackService feedbackService;

    @Autowired
    public ClientServiceFacade(ClientService clientService, HairdresserService hairdresserService,
                               BeautyServiceService beautyServiceService, AppointmentService appointmentService,
                               PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                               TimeSlotService timeSlotService, FeedbackService feedbackService) {
        this.clientService = clientService;
        this.hairdresserService = hairdresserService;
        this.beautyServiceService = beautyServiceService;
        this.appointmentService = appointmentService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.timeSlotService = timeSlotService;
        this.feedbackService = feedbackService;
    }

    /**
     * This method is used for processing the client registration form. It takes in a {@link Client} object,
     * encrypts the password using {@link PasswordEncoder}, assigns the client role, and saves it to the database.
     *
     * @param client A {@link Client} object containing the client's registration details.
     */
    public void processRegistrationForm(Client client) {
        LOGGER.info("Precessing registration form ...");
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRoles(Set.of(roleRepository.findByName("ROLE_CLIENT").orElseThrow()));
        clientService.saveClient(client);
    }

    /**
     * This method fetches all the hairdressers along with the beauty services they provide.
     *
     * @return A list of {@link Hairdresser} objects.
     */
    public List<Hairdresser> getHairdressersWithServices() {
        return hairdresserService.findAllWithBeautyServices();
    }

    /**
     * This method fetches distinct beauty service names.
     *
     * @return A list of distinct beauty service names.
     */
    public List<String> getDistinctServiceNames() {
        return beautyServiceService.findDistinctServiceNames();
    }

    /**
     * This method allows a client to book an appointment. It creates a new {@link Appointment} object and saves it to the database.
     *
     * @param authentication The Authentication object holding the client's authentication details.
     * @param hairdresserId  The ID of the hairdresser.
     * @param serviceName    The name of the service.
     * @param dateTime       The date and time of the appointment.
     */
    public void bookAppointment(Authentication authentication, Long hairdresserId,
                                String serviceName, String dateTime) {
        LOGGER.info("Client is booking an appointment ...");
        String[] dateTimeParts = dateTime.split(" - ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeParts[0], formatter);

        Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
        BeautyService beautyService = beautyServiceService.findFirstByName(serviceName).orElseThrow(
                () -> new ResourceNotFoundException("Service with name " + serviceName + " not found"));
        String userEmail = authentication.getName();

        Client client = clientService.findClientByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("Client not found with email: " + userEmail));

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setHairdresser(hairdresser);
        appointment.setBeautyService(beautyService);
        appointment.setAppointmentTime(appointmentDateTime);
        appointment.setPaymentStatus(PaymentStatus.PENDING);

        appointmentService.saveAppointment(appointment);
    }

    /**
     * This method fetches all the available time slots for a specific hairdresser.
     *
     * @param hairdresserId The ID of the hairdresser.
     * @return A list of available time slots.
     */
    public List<String> getTimeSlots(Long hairdresserId) {
        LOGGER.info("Loading the timeslots of an hairdresser to the view for client ...");
        Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

        List<TimeSlot> timeSlots = timeSlotService.generateTimeSlots(hairdresser);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return timeSlots.stream()
                .map(slot -> formatter.format(slot.getStart()) + " - " + formatter.format(slot.getEnd()))
                .collect(Collectors.toList());
    }

    /**
     * This method fetches all the appointments of a specific client.
     *
     * @param authentication The Authentication object holding the client's authentication details.
     * @return A list of {@link Appointment} objects.
     */
    public List<Appointment> getClientAppointments(Authentication authentication) {
        LOGGER.info("Client's appointments are displayed ...");
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Authentication is required.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Client client = clientService.findClientByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Client not found with email: " + userDetails.getUsername()));

        return appointmentService.findByClient(client);
    }

    /**
     * This method is used to save a client's feedback. It associates the feedback with the client and the appointment,
     * and saves it to the database.
     * <p>
     * This method is annotated with {@link org.springframework.transaction.annotation.Transactional} to ensure that
     * the database operations are completed within a single transaction.
     *
     * @param authentication The Authentication object holding the client's authentication details.
     * @param appointmentId  The ID of the appointment.
     * @param feedback       A {@link Feedback} object containing the feedback details.
     */
    @Transactional
    public void saveFeedback(Authentication authentication, Long appointmentId, Feedback feedback) {
        LOGGER.info("Client is saving the feedback ...");

        Appointment appointment = appointmentService.findById(appointmentId)
                .orElseThrow(
                        () -> new AppointmentNotFoundException("No appointment found with the provided ID."));

        feedback.setAppointment(appointment);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Client client = clientService.findClientByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Client not found with email: " + userDetails.getUsername()));
        feedback.setClient(client);
        feedback.setCreatedAt(LocalDateTime.now());
        System.out.println(feedback);
        feedbackService.createFeedback(feedback);
    }

}