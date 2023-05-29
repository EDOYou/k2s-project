package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.entities.model.wrapper.WorkingHoursWrapper;
import com.edoyou.k2sbeauty.services.facade.HairdresserServiceFacade;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

/**
 * This is a Spring controller class that manages the interactions between the user and the system
 * regarding hairdresser-related functionalities such as viewing appointments, scheduling,
 * and registering a new hairdresser.
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Controller
@RequestMapping("/hairdresser")
public class HairdresserController {

  private final HairdresserServiceFacade hairdresserServiceFacade;
  private final BeautyServiceService beautyServiceService;

  @Autowired
  public HairdresserController(HairdresserServiceFacade hairdresserServiceFacade,
      BeautyServiceService beautyServiceService) {
    this.hairdresserServiceFacade = hairdresserServiceFacade;
    this.beautyServiceService = beautyServiceService;
  }

  /**
   * This method is used to get the appointments of a hairdresser.
   * It returns a view name as a String for Thymeleaf template "hairdresser/hairdresser_appointments".
   *
   * @param authentication The authentication object holding the authenticated user's details.
   * @param model The Spring Model object used to pass attributes to the view.
   * @return String representing the name of the view.
   */
  @GetMapping("/appointments")
  public String getAppointments(Authentication authentication, Model model) {
    var appointments = hairdresserServiceFacade.getAppointments(authentication.getName());
    model.addAttribute("completedAppointments", appointments.completedAppointments());
    model.addAttribute("pendingAppointments", appointments.pendingAppointments());
    return "hairdresser/hairdresser_appointments";
  }

  /**
   * This method is used to mark an appointment as completed.
   * It redirects the user to the appointments page after marking an appointment as completed.
   *
   * @param id The ID of the appointment to be marked as completed.
   * @param authentication The authentication object holding the authenticated user's details.
   * @return String representing the redirect URL.
   */
  @PostMapping("/appointments/{id}/complete")
  public String markAppointmentAsCompleted(@PathVariable Long id, Authentication authentication) {
    hairdresserServiceFacade.completeAppointment(id, authentication.getName());
    return "redirect:/hairdresser/appointments";
  }

  /**
   * This method is used to get the hairdresser's schedule.
   * It returns a view name as a String for Thymeleaf template "hairdresser/schedule".
   *
   * @param authentication The authentication object holding the authenticated user's details.
   * @param model The Spring Model object used to pass attributes to the view.
   * @return String representing the name of the view.
   */
  @GetMapping("/schedule")
  public String getSchedule(Authentication authentication, Model model) {
    Map<LocalDate, List<TimeSlot>> schedule = hairdresserServiceFacade.getSchedule(
        authentication.getName());
    model.addAttribute("schedule", schedule);
    model.addAttribute("timeFormatter", DateTimeFormatter.ofPattern("HH:mm"));
    return "hairdresser/schedule";
  }


  /**
   * This method is used to process the hairdresser registration form.
   * It validates the input, adds any errors to the model, and if no errors,
   * it calls the facade to register the hairdresser.
   *
   * @param hairdresser The Hairdresser object containing the hairdresser's details.
   * @param bindingResultHairdresser The BindingResult object containing validation errors for hairdresser.
   * @param workingHoursWrapper The WorkingHoursWrapper object containing the working hours details.
   * @param bindingResultWorkingHours The BindingResult object containing validation errors for working hours.
   * @param model The Spring Model object used to pass attributes to the view.
   * @return String representing the redirect URL if registration is successful, otherwise returns the form view.
   */
  @PostMapping("/register_hairdresser")
  public String processRegistrationForm(
      @Valid @ModelAttribute("hairdresser") Hairdresser hairdresser,
      BindingResult bindingResultHairdresser,
      @Valid @ModelAttribute("workingHours") WorkingHoursWrapper workingHoursWrapper,
      BindingResult bindingResultWorkingHours,
      Model model) {

    List<String> errors = new ArrayList<>();
    if (bindingResultHairdresser.hasErrors()) {
      bindingResultHairdresser.getAllErrors()
          .forEach(error -> errors.add(error.getDefaultMessage()));
    }

    if (bindingResultWorkingHours.hasErrors()) {
      bindingResultWorkingHours.getAllErrors()
          .forEach(error -> errors.add(error.getDefaultMessage()));
    }

    if (!errors.isEmpty()) {
      model.addAttribute("errors", errors);
      return "/hairdresser/register_hairdresser";
    }

    hairdresserServiceFacade.registerHairdresser(hairdresser,
        workingHoursWrapper.getWorkingHoursMap());
    return "redirect:home";
  }

/**
 * This method is used to show the hairdresser registration form.
 * It adds necessary attributes to the model and returns a view name as a String for Thymeleaf template
 * "hairdresser/register_hairdresser".
 *
 * @param model The Spring Model object used to pass attributes to the view.
 * @return String representing the redirect URL if registration is successful.
 */
  @GetMapping("/register_hairdresser")
  public String showRegistrationForm(Model model) {
    List<BeautyService> services = beautyServiceService.findAll();
    model.addAttribute("services", services);
    model.addAttribute("hairdresser", new Hairdresser());
    model.addAttribute("workingHours", new WorkingHoursWrapper());
    return "/hairdresser/register_hairdresser";
  }

}