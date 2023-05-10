package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ClientController {

  private final ClientService clientService;
  private final HairdresserService hairdresserService;
  private final BeautyServiceService beautyServiceService;
  private final AppointmentService appointmentService;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  @Autowired
  public ClientController(ClientService clientService,
      HairdresserService hairdresserService,
      BeautyServiceService beautyServiceService,
      AppointmentService appointmentService,
      PasswordEncoder passwordEncoder,
      RoleRepository roleRepository) {
    this.clientService = clientService;
    this.hairdresserService = hairdresserService;
    this.beautyServiceService = beautyServiceService;
    this.appointmentService = appointmentService;
    this.passwordEncoder = passwordEncoder;
    this.roleRepository = roleRepository;
  }

  @PostMapping("/register")
  public String processRegistrationForm(@ModelAttribute("client") Client client) {
    client.setPassword(passwordEncoder.encode(client.getPassword()));
    client.setRoles(Set.of(roleRepository.findByName("ROLE_CLIENT").orElseThrow()));
    clientService.saveClient(client);

    System.out.println("VNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + client);

    return "redirect:/client/appointments";
  }

  @GetMapping("/client/book")
  public String showBookingPage(Model model) {
    List<Hairdresser> hairdressers = hairdresserService.findAllWithBeautyServices();
    List<String> services = beautyServiceService.findDistinctServiceNames();
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

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTime, formatter);

    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
    BeautyService beautyService = beautyServiceService.findFirstByName(serviceName)
        .orElseThrow(
            () -> new ResourceNotFoundException("Service with name " + serviceName + " not found"));
    User user = clientService.findUserByEmail(authentication.getName())
        .orElseThrow(() -> new UsernameNotFoundException(
            "User not found with email: " + authentication.getName()));
    Client client = (Client) clientService.findUserById(user.getId())
        .orElseThrow(
            () -> new UsernameNotFoundException("Client not found with id: " + user.getId()));

    Appointment appointment = new Appointment();
    appointment.setClient(client);
    appointment.setHairdresser(hairdresser);
    appointment.setBeautyService(beautyService);
    appointment.setAppointmentTime(appointmentDateTime);

    appointmentService.saveAppointment(appointment);

    return "redirect:/client/appointments";
  }

  @GetMapping("/client/appointments")
  public String viewAppointments(Authentication authentication, Model model) {
    System.out.println("Inside /client/appointments controller");
    if (authentication == null || authentication.getName() == null) {
      return "redirect:/login";
    }
    Client client = (Client) clientService.loadUserByUsername(authentication.getName());
    List<Appointment> appointments = appointmentService.findByClient(client);
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