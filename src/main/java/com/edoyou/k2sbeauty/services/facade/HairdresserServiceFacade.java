package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.dto.AppointmentDTO;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.exceptions.AppointmentNotFoundException;
import com.edoyou.k2sbeauty.exceptions.UnauthorizedActionException;
import com.edoyou.k2sbeauty.exceptions.UserNotFoundException;
import com.edoyou.k2sbeauty.services.implementations.NotificationService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The HairdresserServiceFacade is a service layer that provides a unified interface to a set of interfaces
 * in a subsystem related to hairdresser's operations. It defines a higher-level interface that makes
 * the subsystem easier to use by reducing complexity with a single point of access.
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Service
public class HairdresserServiceFacade {

    public static final Logger LOGGER = LogManager.getLogger(HairdresserServiceFacade.class.getName());
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final BeautyServiceService beautyServiceService;
    private final HairdresserService hairdresserService;
    private final AppointmentService appointmentService;
    private final WorkingHoursService workingHoursService;
    private final NotificationService notificationService;

    /**
     * This constructor is responsible for dependency injection.
     */
    @Autowired
    public HairdresserServiceFacade(PasswordEncoder passwordEncoder, RoleService roleService,
                                    BeautyServiceService beautyServiceService, HairdresserService hairdresserService,
                                    AppointmentService appointmentService, WorkingHoursService workingHoursService,
                                    NotificationService notificationService) {
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.beautyServiceService = beautyServiceService;
        this.hairdresserService = hairdresserService;
        this.appointmentService = appointmentService;
        this.workingHoursService = workingHoursService;
        this.notificationService = notificationService;
    }

    /**
     * Retrieves appointments for a hairdresser.
     * It checks whether the authenticated user is a hairdresser and approved.
     * Then retrieves the completed and pending appointments for the hairdresser.
     *
     * @param email The email of the hairdresser.
     * @return An AppointmentDTO containing a list of completed and pending appointments.
     * @throws UserNotFoundException       If no user is found with the provided email or if the user is not a hairdresser.
     * @throws UnauthorizedActionException If the user exists but is not approved.
     */
    public AppointmentDTO getAppointments(String email) {
        LOGGER.info("Getting appointments for a hairdresser ...");
        Optional<User> userOptional = hairdresserService.findUserByEmail(email);
        if (userOptional.isEmpty() || !(userOptional.get() instanceof Hairdresser hairdresser)) {
            throw new UserNotFoundException("The authenticated user is not a hairdresser.");
        }
        if (!hairdresser.isApproved()) {
            throw new UnauthorizedActionException("User does not exist or is not approved yet.");
        }

        var appointments = appointmentService.findByHairdresser(hairdresser);
        var completedAppointments = appointments.stream().filter(Appointment::isCompleted)
                .collect(Collectors.toList());
        var pendingAppointments = appointments.stream()
                .filter(appointment -> !appointment.isCompleted()).collect(
                        Collectors.toList());

        return new AppointmentDTO(completedAppointments, pendingAppointments);
    }

    /**
     * Retrieves the schedule for a hairdresser.
     *
     * @param email The email of the hairdresser.
     * @return A Map where the keys are dates and the values are lists of TimeSlot.
     * @throws UserNotFoundException If no user is found with the provided email or if the user is not a hairdresser.
     */
    public Map<LocalDate, List<TimeSlot>> getSchedule(String email) {
        LOGGER.info("Getting schedule for a hairdresser ...");
        Hairdresser hairdresser = getAuthenticatedHairdresser(email);
        return hairdresserService.generateSchedule(hairdresser);
    }

    /**
     * Marks an appointment as completed.
     * It checks whether the authenticated user is a hairdresser and related to the appointment.
     * Then updates the appointment to set it as completed and sends a notification to the client.
     *
     * @param id    The ID of the appointment.
     * @param email The email of the hairdresser.
     * @throws UserNotFoundException        If no user is found with the provided email or if the user is not a hairdresser.
     * @throws AppointmentNotFoundException If no appointment is found with the provided ID.
     * @throws UnauthorizedActionException  If the authenticated hairdresser is not related to the appointment.
     */
    public void completeAppointment(Long id, String email) {
        LOGGER.info("Hairdresser completed the appointment ...");
        Hairdresser hairdresser = getAuthenticatedHairdresser(email);

        Optional<Appointment> appointmentOptional = appointmentService.findById(id);
        if (appointmentOptional.isEmpty()) {
            throw new AppointmentNotFoundException("No appointment found with the provided ID.");
        }

        Appointment appointment = appointmentOptional.get();
        if (!appointment.getHairdresser().equals(hairdresser)) {
            throw new UnauthorizedActionException(
                    "The authenticated hairdresser is not related to the appointment.");
        }

        appointment.setCompleted(true);
        appointmentService.saveAppointment(appointment);
        notifyClient(appointment);
    }

    /**
     * Sends a notification to a client regarding an appointment.
     * This method schedules a notification to be sent to the client's email.
     *
     * @param appointment The appointment object for which the client is to be notified.
     */
    private void notifyClient(Appointment appointment) {
        LOGGER.info("Email sent to the client for feedback ...");
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            String clientEmail = appointment.getClient().getEmail();
            String subject = "We'd love to hear your feedback!";
            String text = "http://localhost:8080/login?redirectUrl=/client/feedback?appointmentId=" + appointment.getId();
            notificationService.sendNotification(clientEmail, subject, text);
        }, 1, TimeUnit.DAYS);
    }

    /**
     * Fetches the authenticated hairdresser based on the provided email.
     *
     * @param email The email of the authenticated hairdresser.
     * @return The authenticated Hairdresser.
     * @throws UserNotFoundException If no user is found with the provided email or if the user is not a hairdresser.
     */
    private Hairdresser getAuthenticatedHairdresser(String email) {
        Optional<User> userOptional = hairdresserService.findUserByEmail(email);
        if (userOptional.isEmpty() || !(userOptional.get() instanceof Hairdresser hairdresser)) {
            throw new UserNotFoundException("The authenticated user is not a hairdresser.");
        }
        return hairdresser;
    }

    /**
     * Registers a new hairdresser.
     * It encodes the password, assigns the hairdresser role, sets the services,
     * and adds working hours for the hairdresser. Finally, it saves the hairdresser in the database.
     *
     * @param hairdresser        The Hairdresser object containing the hairdresser's details.
     * @param workingHoursDtoMap A Map where the keys are day of the week index and the values are WorkingHoursDTO objects.
     */
    public void registerHairdresser(Hairdresser hairdresser,
                                    Map<Integer, WorkingHoursDTO> workingHoursDtoMap) {
        LOGGER.info("Registering the hairdresser ...");

        hairdresser.setPassword(passwordEncoder.encode(hairdresser.getPassword()));
        hairdresser.setRoles(Set.of(roleService.getRoleByName("ROLE_HAIRDRESSER").orElseThrow()));

        var services = beautyServiceService.findAllByIdIn(
                hairdresser.getSelectedServiceIds());
        hairdresser.setBeautyServices(new HashSet<>(services));

        for (Map.Entry<Integer, WorkingHoursDTO> entry : workingHoursDtoMap.entrySet()) {
            if (entry.getValue().getStart() != null && entry.getValue().getEnd() != null) {

                WorkingHours workingHours = findOrCreateWorkingHours(entry.getKey(), entry.getValue());

                workingHours.getHairdressers().add(hairdresser);
                hairdresser.getWorkingHours().add(workingHours);

                workingHoursService.saveWorkingHours(workingHours);
            }
        }
        hairdresserService.saveHairdresser(hairdresser);
    }

    /**
     * Fetches or creates a WorkingHours entity.
     * This method checks if a WorkingHours entity with the same start and end times on the same day of the week exists.
     * If it exists, it returns the existing WorkingHours.
     * If it doesn't exist, it creates a new WorkingHours entity with the provided details.
     *
     * @param dayOfWeekIndex  The index of the day of the week. Indexes start from 0 (Monday).
     * @param workingHoursDto The WorkingHoursDTO containing the start and end times.
     * @return The existing or newly created WorkingHours entity.
     */
    private WorkingHours findOrCreateWorkingHours(Integer dayOfWeekIndex,
                                                  WorkingHoursDTO workingHoursDto) {
        LOGGER.info("Finding if exists already in database, otherwise create a working hours ...");
        Optional<WorkingHours> existingWorkingHours = workingHoursService.findByDayOfWeekAndStartAndEnd(
                DayOfWeek.values()[dayOfWeekIndex], workingHoursDto.getStart(), workingHoursDto.getEnd());

        if (existingWorkingHours.isPresent()) {
            return existingWorkingHours.get();
        } else {
            WorkingHours workingHours = new WorkingHours();
            workingHours.setDayOfWeek(DayOfWeek.values()[dayOfWeekIndex]);
            workingHours.setStart(workingHoursDto.getStart());
            workingHours.setEnd(workingHoursDto.getEnd());
            return workingHours;
        }
    }

}