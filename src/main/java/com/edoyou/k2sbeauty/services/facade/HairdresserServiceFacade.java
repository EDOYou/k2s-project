package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.dto.AppointmentDTO;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
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
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HairdresserServiceFacade {

  public static final Logger LOGGER = Logger.getLogger(HairdresserServiceFacade.class.getName());
  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final BeautyServiceService beautyServiceService;
  private final HairdresserService hairdresserService;
  private final AppointmentService appointmentService;
  private final WorkingHoursService workingHoursService;
  private final NotificationService notificationService;
  private final FeedbackService feedbackService;


  @Autowired
  public HairdresserServiceFacade(PasswordEncoder passwordEncoder, RoleService roleService,
      BeautyServiceService beautyServiceService, HairdresserService hairdresserService,
      AppointmentService appointmentService, WorkingHoursService workingHoursService,
      NotificationService notificationService,
      FeedbackService feedbackService) {
    this.passwordEncoder = passwordEncoder;
    this.roleService = roleService;
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
    this.appointmentService = appointmentService;
    this.workingHoursService = workingHoursService;
    this.notificationService = notificationService;
    this.feedbackService = feedbackService;
  }

  public AppointmentDTO getAppointments(String email) {
    Optional<User> userOptional = hairdresserService.findUserByEmail(email);
    if (userOptional.isEmpty() || !(userOptional.get() instanceof Hairdresser hairdresser)) {
      throw new UserNotFoundException("The authenticated user is not a hairdresser.");
    }
    if (!hairdresser.isApproved()) {
      throw new UnauthorizedActionException("User does not exist or is not approved yet.");
    }

    List<Appointment> appointments = appointmentService.findByHairdresser(hairdresser);
    List<Appointment> completedAppointments = appointments.stream().filter(Appointment::isCompleted)
        .collect(Collectors.toList());
    List<Appointment> pendingAppointments = appointments.stream()
        .filter(appointment -> !appointment.isCompleted()).collect(
            Collectors.toList());

    return new AppointmentDTO(completedAppointments, pendingAppointments);
  }

  public Map<LocalDate, List<TimeSlot>> getSchedule(String email) {
    Hairdresser hairdresser = getAuthenticatedHairdresser(email);
    return hairdresserService.generateSchedule(hairdresser);
  }

  public void completeAppointment(Long id, String email) {
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
    hairdresserService.updateRating(hairdresser);
  }

  private void notifyClient(Appointment appointment) {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.schedule(() -> {
      String clientEmail = appointment.getClient().getEmail();
      LOGGER.info("Client e-mail : " + clientEmail);
      String subject = "We'd love to hear your feedback!";
      String text = "http://localhost:8080/client/feedback";
      notificationService.sendNotification(clientEmail, subject, text);
    }, 0, TimeUnit.DAYS);
  }

  private Hairdresser getAuthenticatedHairdresser(String email) {
    Optional<User> userOptional = hairdresserService.findUserByEmail(email);
    if (userOptional.isEmpty() || !(userOptional.get() instanceof Hairdresser hairdresser)) {
      throw new UserNotFoundException("The authenticated user is not a hairdresser.");
    }
    return hairdresser;
  }

  public void registerHairdresser(Hairdresser hairdresser,
      Map<Integer, WorkingHoursDTO> workingHoursDtoMap) {

    hairdresser.setPassword(passwordEncoder.encode(hairdresser.getPassword()));
    hairdresser.setRoles(Set.of(roleService.getRoleByName("ROLE_HAIRDRESSER").orElseThrow()));

    List<BeautyService> services = beautyServiceService.findAllByIdIn(
        hairdresser.getSelectedServiceIds());
    hairdresser.setBeautyServices(new HashSet<>(services));

    for (Map.Entry<Integer, WorkingHoursDTO> entry : workingHoursDtoMap.entrySet()) {
      if (entry.getValue().getStart() != null && entry.getValue().getEnd() != null) {

        WorkingHours workingHours = findOrCreateWorkingHours(entry.getKey(), entry.getValue());

        workingHours.getHairdressers().add(hairdresser);
        hairdresser.getWorkingHours().add(workingHours);

        workingHoursService.saveWorkingHours(workingHours);
        hairdresserService.saveHairdresser(hairdresser);
      }
    }

  }

  private WorkingHours findOrCreateWorkingHours(Integer dayOfWeekIndex,
      WorkingHoursDTO workingHoursDto) {
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