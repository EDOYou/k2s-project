package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hairdresser")
public class HairdresserController {

  private final static Logger LOGGER = Logger.getLogger(HairdresserController.class.getName());
  private final AppointmentService appointmentService;
  private final HairdresserService hairdresserService;

  @Autowired
  public HairdresserController(AppointmentService appointmentService, HairdresserService hairdresserService) {
    this.appointmentService = appointmentService;
    this.hairdresserService = hairdresserService;
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

    model.addAttribute("appointments", appointments);

    return "hairdresser/hairdresser_appointments";
  }
}