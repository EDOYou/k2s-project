package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.annotation.AtLeastOneDay;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.entities.model.wrapper.WorkingHoursWrapper;
import com.edoyou.k2sbeauty.exceptions.AppointmentNotFoundException;
import com.edoyou.k2sbeauty.exceptions.UnauthorizedActionException;
import com.edoyou.k2sbeauty.exceptions.UserNotFoundException;
import com.edoyou.k2sbeauty.services.facade.HairdresserServiceFacade;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hairdresser")
public class HairdresserController {

  private final static Logger LOGGER = Logger.getLogger(HairdresserController.class.getName());
  private final AppointmentService appointmentService;
  private final HairdresserService hairdresserService;
  private final HairdresserServiceFacade hairdresserServiceFacade;
  private final BeautyServiceService beautyServiceService;

  @Autowired
  public HairdresserController(AppointmentService appointmentService,
      HairdresserService hairdresserService, HairdresserServiceFacade hairdresserServiceFacade,
      BeautyServiceService beautyServiceService) {
    this.appointmentService = appointmentService;
    this.hairdresserService = hairdresserService;
    this.hairdresserServiceFacade = hairdresserServiceFacade;
    this.beautyServiceService = beautyServiceService;
  }

  @GetMapping("/appointments")
  public String getAppointments(Authentication authentication, Model model) {
    if (authentication == null || authentication.getName() == null) {
      return "redirect:/login";
    }

    Optional<User> userOptional = hairdresserService.findUserByEmail(authentication.getName());

    if (userOptional.isEmpty() || !(userOptional.get() instanceof Hairdresser hairdresser)) {
      LOGGER.info("User cannot be empty.");
      return "redirect:/login";
    }

    if (!hairdresser.isApproved()) {
      LOGGER.info("User does not exist or is not approved yet.");
      return "redirect:/not_approved";
    }
    List<Appointment> appointments = appointmentService.findByHairdresser(hairdresser);
    List<Appointment> completedAppointments = appointments.stream().filter(Appointment::isCompleted)
        .collect(Collectors.toList());
    List<Appointment> pendingAppointments = appointments.stream()
        .filter(appointment -> !appointment.isCompleted()).collect(Collectors.toList());

    model.addAttribute("completedAppointments", completedAppointments);
    model.addAttribute("pendingAppointments", pendingAppointments);
    return "hairdresser/hairdresser_appointments";
  }

  @PostMapping("/appointments/{id}/complete")
  public String markAppointmentAsCompleted(@PathVariable Long id, Authentication authentication) {
    Optional<User> userOptional = hairdresserService.findUserByEmail(authentication.getName());

    if (userOptional.isEmpty() || !(userOptional.get() instanceof Hairdresser hairdresser)) {
      throw new UserNotFoundException("The authenticated user is not a hairdresser.");
    }

    Optional<Appointment> appointmentOptional = appointmentService.findById(id);

    if (appointmentOptional.isEmpty()) {
      throw new AppointmentNotFoundException("No appointment found with the provided ID.");
    }

    Appointment appointment = appointmentOptional.get();

    if (!appointment.getHairdresser().equals(hairdresser)) {
      throw new UnauthorizedActionException(
          "The authenticated hairdresser is not related to the appointment.");
    }

    hairdresserServiceFacade.completeAppointment(id);
    return "redirect:/hairdresser/appointments";
  }

  @GetMapping("/schedule")
  public String getSchedule(Authentication authentication, Model model) {
    Optional<User> userOptional = hairdresserService.findUserByEmail(authentication.getName());

    if (userOptional.isEmpty() || !(userOptional.get() instanceof Hairdresser hairdresser)) {
      throw new UserNotFoundException("The authenticated user is not a hairdresser.");
    }

    List<TimeSlot> timeSlots = hairdresserService.getSchedule(hairdresser);

    model.addAttribute("timeSlots", timeSlots);
    return "hairdresser/schedule";
  }


  @PostMapping("/register_hairdresser")
  public String processRegistrationForm(@Valid @ModelAttribute("hairdresser") Hairdresser hairdresser,
      BindingResult bindingResult,
      @Valid @ModelAttribute("workingHours") WorkingHoursWrapper workingHoursWrapper) {

    if (bindingResult.hasErrors()) {
      LOGGER.warning("Binding result has errors: " + bindingResult.getAllErrors());
      // handle errors, e.g. return the registration form with error messages
      return "/hairdresser/register_hairdresser";
    }
    hairdresserServiceFacade.registerHairdresser(hairdresser, workingHoursWrapper.getWorkingHoursMap());
    return "redirect:home";
  }

  @GetMapping("/register_hairdresser")
  public String showRegistrationForm(Model model) {
    List<BeautyService> services = beautyServiceService.findAll();
    model.addAttribute("services", services);
    model.addAttribute("hairdresser", new Hairdresser());
    model.addAttribute("workingHours", new WorkingHoursWrapper());
    return "/hairdresser/register_hairdresser";
  }
}