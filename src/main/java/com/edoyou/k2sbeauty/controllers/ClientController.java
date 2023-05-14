package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.facade.ClientServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ClientController {

  private final ClientServiceFacade clientServiceFacade;

  @Autowired
  public ClientController(ClientServiceFacade clientServiceFacade) {
    this.clientServiceFacade = clientServiceFacade;
  }

  @PostMapping("/register")
  public String processRegistrationForm(@ModelAttribute("client") Client client) {
    clientServiceFacade.processRegistrationForm(client);
    return "redirect:/client/appointments";
  }

  @GetMapping("/client/book")
  public String showBookingPage(Model model) {
    List<Hairdresser> hairdressers = clientServiceFacade.getHairdressersWithServices();
    List<String> services = clientServiceFacade.getDistinctServiceNames();
    model.addAttribute("hairdressers", hairdressers);
    model.addAttribute("services", services);
    return "client/book";
  }

  @PostMapping("/client/book")
  public String bookAppointment(Authentication authentication,
      @RequestParam("hairdresserId") Long hairdresserId,
      @RequestParam("serviceName") String serviceName,
      @RequestParam("dateTime") String dateTime) {

    if (dateTime.isEmpty()) {
      return "redirect:/client/book";
    }

    clientServiceFacade.bookAppointment(authentication, hairdresserId, serviceName, dateTime);
    return "redirect:/client/appointments";
  }

  @GetMapping("/client/timeslots")
  public ResponseEntity<List<String>> getTimeSlots(
      @RequestParam("hairdresserId") Long hairdresserId) {
    List<String> timeSlotStrings = clientServiceFacade.getTimeSlots(hairdresserId);
    return ResponseEntity.ok(timeSlotStrings);
  }


  @GetMapping("/client/appointments")
  public String viewAppointments(Authentication authentication, Model model) {
    if (authentication == null || authentication.getName() == null) {
      return "redirect:/login";
    }

    List<Appointment> appointments = clientServiceFacade.getClientAppointments(authentication);
    model.addAttribute("appointments", appointments);
    return "client/appointments";
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("client", new Client());
    return "register";
  }

  @GetMapping("/login")
  public String showLoginPage() {
    return "login";
  }
}