package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.implementations.appointment_details.TimeSlotService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides the concrete implementation of the {@code HairdresserService} interface. This service manages
 * operations related to the {@code Hairdresser} entity.
 *
 * <p>This service is responsible for saving, finding, and deleting Hairdresser records, generating
 * hairdresser schedules, and updating their ratings. It interacts with the data layer via
 * {@code HairdresserRepository}, {@code AppointmentService}, {@code TimeSlotService}, and {@code FeedbackService}.
 *
 * <p><strong>Note:</strong> The service is annotated with {@code @Service}, allowing it to be automatically
 * detected by Spring's component scanning.
 *
 * <p>Typical usage:
 * <pre>{@code
 * Hairdresser hairdresser = new Hairdresser();
 * // populate hairdresser object
 * hairdresserService.saveHairdresser(hairdresser);
 * }</pre>
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Service
public class HairdresserServiceImpl extends UserServiceImpl implements HairdresserService {

    private static final Logger LOGGER = LogManager.getLogger(HairdresserServiceImpl.class.getName());

    private final HairdresserRepository hairdresserRepository;
    private final AppointmentService appointmentService;
    private final TimeSlotService timeSlotService;
    private final FeedbackService feedbackService;

    /**
     * Constructs a new HairdresserService. This constructor is annotated with {@code @Autowired}, so Spring
     * automatically injects the dependencies.
     *
     * @param userRepository        the repository that this service will use for data access
     * @param hairdresserRepository the repository to manage {@code Hairdresser} entities
     * @param appointmentService    the service that this service will use for managing {@code Appointment} entities
     * @param timeSlotService       the service that this service will use for managing {@code TimeSlot} entities
     * @param feedbackService       the service that this service will use for managing {@code Feedback} entities
     */
    @Autowired
    public HairdresserServiceImpl(UserRepository userRepository,
                                  HairdresserRepository hairdresserRepository,
                                  AppointmentService appointmentService,
                                  TimeSlotService timeSlotService,
                                  FeedbackService feedbackService) {
        super(userRepository);
        this.hairdresserRepository = hairdresserRepository;
        this.appointmentService = appointmentService;
        this.timeSlotService = timeSlotService;
        this.feedbackService = feedbackService;
    }

    /**
     * Saves a {@link Hairdresser} entity to the database.
     *
     * @param hairdresser The {@link Hairdresser} entity to be saved.
     * @throws NullPointerException If the hairdresser object is null.
     */
    @Override
    public void saveHairdresser(Hairdresser hairdresser) {
        LOGGER.info("Saving the hairdresser ...");
        if (hairdresser == null) {
            throw new NullPointerException("Hairdresser cannot be null");
        }
        hairdresserRepository.save(hairdresser);
    }


    /**
     * Retrieves a list of all {@link Hairdresser} entities with the specified sort parameter.
     *
     * @param sortBy The attribute by which the returned list should be sorted.
     * @return A list of all {@link Hairdresser} entities sorted by the specified attribute.
     */
    @Override
    public List<Hairdresser> findAllHairdressers(String sortBy) {
        LOGGER.info("Find all hairdressers with sort by...");
        if (sortBy == null) {
            return hairdresserRepository.findByIsApprovedTrue();
        }

        if ("serviceName".equalsIgnoreCase(sortBy) || "price".equalsIgnoreCase(sortBy)) {
            return findAllHairdressersByServiceId(sortBy, null);
        }

        Sort sort = createSortBy(sortBy);
        return hairdresserRepository.findByIsApprovedTrue(sort);
    }

    /**
     * Retrieves a list of all {@link Hairdresser} entities from the database.
     *
     * @return A list of all {@link Hairdresser} entities.
     */
    @Override
    public List<Hairdresser> findAllHairdressers() {
        LOGGER.info("Find all hairdressers ...");
        return hairdresserRepository.findAll();
    }

    /**
     * Retrieves a list of {@link Hairdresser} entities by service ID, sorted by the specified attribute.
     *
     * @param sortBy    The attribute by which the returned list should be sorted.
     * @param serviceId The ID of the beauty service by which to filter the {@link Hairdresser} entities.
     * @return A list of {@link Hairdresser} entities sorted by the specified attribute and filtered by the specified service ID.
     */
    @Override
    public List<Hairdresser> findAllHairdressersByServiceId(String sortBy, Long serviceId) {
        LOGGER.info("Find all hairdressers by service ID...");
        Sort sort;
        if (sortBy == null || "serviceName".equalsIgnoreCase(sortBy)) {
            sort = createSortByServiceNameAndPrice();
        } else {
            sort = createSortBy(sortBy);
        }

        return hairdresserRepository.findAllByService(serviceId, sort);
    }

    /**
     * Retrieves a list of {@link Hairdresser} entities with beauty services from the database.
     *
     * @return A list of {@link Hairdresser} entities with beauty services.
     */
    @Override
    public List<Hairdresser> findAllWithBeautyServices() {
        LOGGER.info("Find all hairdressers with beauty services ...");
        return hairdresserRepository.findAllWithBeautyServices();
    }

    /**
     * Retrieves a page of {@link Hairdresser} entities with beauty services from the database, according to the provided {@link Pageable}.
     *
     * @param pageable A {@link Pageable} object specifying the size and sorting of the page to be returned.
     * @return A page of {@link Hairdresser} entities with beauty services.
     */
    @Override
    public Page<Hairdresser> findAllWithBeautyServices(Pageable pageable) {
        return hairdresserRepository.findAllWithBeautyServices(pageable);
    }

    /**
     * Retrieves a {@link Hairdresser} entity by its ID.
     *
     * @param id The ID of the {@link Hairdresser} entity to be retrieved.
     * @return The {@link Hairdresser} entity with the specified ID.
     * @throws ResourceNotFoundException if no {@link Hairdresser} entity with the specified ID is found.
     */
    @Override
    public Hairdresser findById(Long id) {
        LOGGER.info("Find hairdresser by ID ...");
        return hairdresserRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Hairdresser with id " + id + " not found"));
    }

    /**
     * Creates a {@link Sort} object by the specified attribute. Currently, only 'serviceName' is supported.
     *
     * @return A {@link Sort} object.
     * @throws IllegalArgumentException if the specified attribute is not supported for sorting.
     */
    private Sort createSortByServiceNameAndPrice() {
        LOGGER.info("In hairdresser service impl., creating Sort ...");
        return Sort.by(Sort.Direction.ASC, "s.name");
    }

    /**
     * Creates a {@link Sort} object by the specified attribute. Currently, 'lastName' and 'rating' are supported.
     *
     * @param sortBy The attribute by which to sort.
     * @return A {@link Sort} object.
     * @throws IllegalArgumentException if the specified attribute is not supported for sorting.
     */
    public Sort createSortBy(String sortBy) {
        LOGGER.info("Sorting by last name or rating ...");
        if ("lastName".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.ASC, "lastName");
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.DESC, "rating");
        } else {
            throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }
    }

    /**
     * Deletes a {@link Hairdresser} entity by its ID.
     *
     * @param id The ID of the {@link Hairdresser} entity to be deleted.
     * @throws ResourceNotFoundException if no {@link Hairdresser} entity with the specified ID is found.
     */
    @Override
    public void deleteHairdresser(Long id) {
        LOGGER.info("Firing hairdresser.");
        Hairdresser hairdresserToDelete = hairdresserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hairdresser not found with ID: " + id));

        hairdresserRepository.delete(hairdresserToDelete);
    }

    /**
     * Retrieves a page of {@link Hairdresser} entities by their approval status, according to the provided {@link Pageable}.
     *
     * @param isApproved The approval status by which to filter the {@link Hairdresser} entities.
     * @param pageable   A {@link Pageable} object specifying the size and sorting of the page to be returned.
     * @return A page of {@link Hairdresser} entities with the specified approval status.
     */
    @Override
    public Page<Hairdresser> findAllHairdressersByApprovalStatus(boolean isApproved,
                                                                 Pageable pageable) {
        LOGGER.info("Finding all hairdressers by their approval status ...");
        return hairdresserRepository.findByIsApproved(isApproved, pageable);
    }

    /**
     * Generates the schedule for a given hairdresser. The schedule consists of a map where keys are dates and
     * values are lists of time slots.
     *
     * <p>This method first generates time slots for the given hairdresser, and retrieves all the appointments
     * for the hairdresser. It then goes through each appointment and, if the appointment is not completed, it
     * assigns it to the time slots that intersect with the appointment time. Finally, it groups the time slots
     * by date and returns the resulting map.
     *
     * @param hairdresser the hairdresser for whom to generate the schedule
     * @return a map representing the schedule of the hairdresser
     * @throws NullPointerException if the given hairdresser is {@code null}
     */
    @Override
    public Map<LocalDate, List<TimeSlot>> generateSchedule(Hairdresser hairdresser) {
        LOGGER.info("Generating a hairdresser schedule ...");
        var timeSlots = timeSlotService.generateTimeSlots(hairdresser);
        var appointments = appointmentService.findByHairdresser(hairdresser);
        Map<LocalDate, List<TimeSlot>> schedule = new TreeMap<>();

        for (Appointment appointment : appointments) {
            if (!appointment.isCompleted()) {
                for (TimeSlot timeSlot : timeSlots) {
                    if (!timeSlot.getStart().isAfter(appointment.getAppointmentTime()) &&
                            !timeSlot.getEnd().isBefore(appointment.getAppointmentTime()
                                    .plusMinutes(appointment.getBeautyService().getDuration()).plusSeconds(1))) {
                        timeSlot.setAppointment(appointment);
                    }
                }
            }
        }

        for (TimeSlot timeSlot : timeSlots) {
            var date = timeSlot.getStart().toLocalDate();
            schedule.computeIfAbsent(date, k -> new ArrayList<>()).add(timeSlot);
        }

        return schedule;
    }

    /**
     * Updates the rating of a {@link Hairdresser} by calculating the average rating of the feedback received for the completed appointments.
     *
     * @param hairdresser The {@link Hairdresser} whose rating should be updated.
     * @throws ResourceNotFoundException if no {@link Hairdresser} entity with the specified ID is found.
     */
    @Override
    public void updateRating(Hairdresser hairdresser) {
        LOGGER.info("Updating the hairdresser's rating ...");
        var hairdresserWithAppointments = hairdresserRepository.findByIdWithAppointments(
                        hairdresser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hairdresser with id " + hairdresser.getId() + " not found"));

        double totalRating = 0.0;
        int numberOfRatings = 0;

        for (Appointment appointment : hairdresserWithAppointments.getAppointments()) {
            Optional<Feedback> optionalFeedback = feedbackService.getFeedbackById(appointment.getId());
            if (optionalFeedback.isPresent()) {
                Feedback feedback = optionalFeedback.get();
                totalRating += feedback.getRating();
                numberOfRatings++;
            }
        }

        if (numberOfRatings > 0) {
            double averageRating = totalRating / numberOfRatings;
            hairdresser.setRating(averageRating);
            hairdresserRepository.save(hairdresser);
        }
    }

}