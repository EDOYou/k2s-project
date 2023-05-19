package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.entities.model.wrapper.WorkingHoursWrapper;
import com.edoyou.k2sbeauty.exceptions.UnauthorizedActionException;
import com.edoyou.k2sbeauty.services.facade.HairdresserServiceFacade;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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
  private final HairdresserServiceFacade hairdresserServiceFacade;
  private final BeautyServiceService beautyServiceService;

  @Autowired
  public HairdresserController(HairdresserServiceFacade hairdresserServiceFacade,
      BeautyServiceService beautyServiceService) {
    this.hairdresserServiceFacade = hairdresserServiceFacade;
    this.beautyServiceService = beautyServiceService;
  }

  @GetMapping("/appointments")
  public String getAppointments(Authentication authentication, Model model) {
    try {
      var appointments = hairdresserServiceFacade.getAppointments(authentication.getName());
      model.addAttribute("completedAppointments", appointments.getCompletedAppointments());
      model.addAttribute("pendingAppointments", appointments.getPendingAppointments());
      return "hairdresser/hairdresser_appointments";
    } catch (Exception ex) {
      LOGGER.info(ex.getMessage());
      return ex instanceof UnauthorizedActionException ? "redirect:/not_approved"
          : "redirect:/login";
    }
  }

  @PostMapping("/appointments/{id}/complete")
  public String markAppointmentAsCompleted(@PathVariable Long id, Authentication authentication) {
    hairdresserServiceFacade.completeAppointment(id, authentication.getName());
    return "redirect:/hairdresser/appointments";
  }

  @GetMapping("/schedule")
  public String getSchedule(Authentication authentication, Model model) {
    Map<LocalDate, List<TimeSlot>> schedule = hairdresserServiceFacade.getSchedule(
        authentication.getName());
    model.addAttribute("schedule", schedule);
    model.addAttribute("timeFormatter", DateTimeFormatter.ofPattern("HH:mm"));
    return "hairdresser/schedule";
  }


  @PostMapping("/register_hairdresser")
  public String processRegistrationForm(
      @Valid @ModelAttribute("hairdresser") Hairdresser hairdresser,
      BindingResult bindingResult,
      @Valid @ModelAttribute("workingHours") WorkingHoursWrapper workingHoursWrapper) {

    if (bindingResult.hasErrors()) {
      LOGGER.warning("Binding result has errors: " + bindingResult.getAllErrors());
      return "/hairdresser/register_hairdresser";
    }
    hairdresserServiceFacade.registerHairdresser(hairdresser,
        workingHoursWrapper.getWorkingHoursMap());
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