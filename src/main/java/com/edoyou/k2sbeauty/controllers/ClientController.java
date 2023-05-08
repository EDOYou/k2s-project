package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

  private final ClientService clientService;
  private final HairdresserService hairdresserService;
  private final BeautyServiceService beautyServiceService;
  private final AppointmentService appointmentService;
  //private static final Logger logger = Logger.getLogger(ClientController.class.getName());

  @Autowired
  public ClientController(ClientService clientService,
      HairdresserService hairdresserService,
      BeautyServiceService beautyServiceService,
      AppointmentService appointmentService) {
    this.clientService = clientService;
    this.hairdresserService = hairdresserService;
    this.beautyServiceService = beautyServiceService;
    this.appointmentService = appointmentService;
  }

  @GetMapping("/book")
  public String showBookingPage(Model model) {
    List<Hairdresser> hairdressers = hairdresserService.findAllWithBeautyServices();
    List<BeautyService> services = beautyServiceService.findAll();
    model.addAttribute("hairdressers", hairdressers);
    model.addAttribute("services", services);
    return "client/book";
  }

  @PostMapping("/book")
  public String bookAppointment(Authentication authentication,
      @RequestParam("hairdresserId") Long hairdresserId,
      @RequestParam("serviceId") Long serviceId,
      @RequestParam("dateTime") String dateTime) {

    //logger.info("Attempting to book an appointment for user: {"+authentication.getName()+"}");

    if (dateTime.isEmpty()) {
      return "redirect:/client/book";
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTime, formatter);

    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
    BeautyService beautyService = beautyServiceService.findById(serviceId)
        .orElseThrow(
            () -> new IllegalArgumentException("Service with id " + serviceId + " not found"));
    User user = clientService.findUserByEmail(authentication.getName())
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authentication.getName()));
    Client client = (Client) clientService.findUserById(user.getId())
        .orElseThrow(() -> new UsernameNotFoundException("Client not found with id: " + user.getId()));


    Appointment appointment = new Appointment();
    appointment.setClient(client);
    appointment.setHairdresser(hairdresser);
    appointment.setBeautyService(beautyService);
    appointment.setAppointmentTime(appointmentDateTime);

    appointmentService.saveAppointment(appointment);

    return "redirect:/client/appointments";
  }

  @GetMapping("/appointments")
  public String viewAppointments(Authentication authentication, Model model) {
    if (authentication == null || authentication.getName() == null) {
      return "redirect:/client/login";
    }
    Client client = (Client) clientService.loadUserByUsername(authentication.getName());
    List<Appointment> appointments = appointmentService.findByClient(client);
    model.addAttribute("appointments", appointments);
    return "client/appointments";
  }

  @GetMapping("/login")
  public String showLoginPage() {
    return "login";
  }
}