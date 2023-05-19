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
 * HairdresserServiceImpl is the implementation of the
 * {@link com.edoyou.k2sbeauty.services.interfaces.HairdresserService} interface. This class
 * provides the necessary functionality for handling Hairdresser related operations such as finding
 * hairdressers by specialization.
 *
 * @see Hairdresser
 */
@Service
public class HairdresserServiceImpl extends UserServiceImpl implements HairdresserService {

  private static final Logger LOGGER = LogManager.getLogger(HairdresserServiceImpl.class.getName());

  private final HairdresserRepository hairdresserRepository;
  private final AppointmentService appointmentService;
  private final TimeSlotService timeSlotService;
  private final FeedbackService feedbackService;

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


  @Override
  public void saveHairdresser(Hairdresser hairdresser) {
    LOGGER.info("Saving the hairdresser ...");
    hairdresserRepository.save(hairdresser);
  }

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

  @Override
  public List<Hairdresser> findAllHairdressers() {
    LOGGER.info("Find all hairdressers ...");
    return hairdresserRepository.findAll();
  }

  /**
   * Finds all hairdressers filtered by service ID and sorted by the given criteria.
   *
   * @param sortBy    The field to sort hairdressers by.
   * @param serviceId The ID of the beauty service to filter hairdressers by.
   * @return A list of hairdressers sorted by the given criteria and filtered by the given service
   * ID.
   */
  @Override
  public List<Hairdresser> findAllHairdressersByServiceId(String sortBy, Long serviceId) {
    LOGGER.info("Find all hairdressers by service ID...");
    Sort sort;
    if ("serviceName".equalsIgnoreCase(sortBy)) {
      sort = createSortByServiceNameAndPrice(sortBy);
    } else {
      sort = createSortBy(sortBy);
    }

    return hairdresserRepository.findAllByService(serviceId, sort);
  }

  @Override
  public List<Hairdresser> findAllWithBeautyServices() {
    LOGGER.info("Find all hairdressers with beauty services ...");
    return hairdresserRepository.findAllWithBeautyServices();
  }

  @Override
  public Page<Hairdresser> findAllWithBeautyServices(Pageable pageable) {
    return hairdresserRepository.findAllWithBeautyServices(pageable);
  }

  @Override
  public Hairdresser findById(Long id) {
    LOGGER.info("Find hairdresser by ID ...");
    return hairdresserRepository.findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Hairdresser with id " + id + " not found"));
  }

  private Sort createSortByServiceNameAndPrice(String sortBy) {
    LOGGER.info("In hairdresser service impl., creating Sort ...");
    if (sortBy.equals("serviceName")) {
      return Sort.by(Sort.Direction.ASC, "s.name");
    } else {
      LOGGER.info("Was trying to sort by service name :(");
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
  }

  private Sort createSortBy(String sortBy) {
    LOGGER.info("Sorting by last name or rating ...");
    if ("lastName".equalsIgnoreCase(sortBy)) {
      return Sort.by(Sort.Direction.ASC, "lastName");
    } else if ("rating".equalsIgnoreCase(sortBy)) {
      return Sort.by(Sort.Direction.DESC, "rating");
    } else {
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
  }

  @Override
  public void deleteHairdresser(Long id) {
    LOGGER.info("Firing hairdresser.");
    Hairdresser hairdresserToDelete = hairdresserRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hairdresser not found with ID: " + id));

    hairdresserRepository.delete(hairdresserToDelete);
  }

  @Override
  public Page<Hairdresser> findAllHairdressersByApprovalStatus(boolean isApproved,
      Pageable pageable) {
    LOGGER.info("Finding all hairdressers by their approval status ...");
    return hairdresserRepository.findByIsApproved(isApproved, pageable);
  }

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