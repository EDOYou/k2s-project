package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.services.implementations.appointment_details.TimeSlotService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

  private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());

  private final ClientService clientService;
  private final HairdresserService hairdresserService;
  private final BeautyServiceService beautyServiceService;
  private final AppointmentService appointmentService;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final TimeSlotService timeSlotService;

  @Autowired
  public ClientController(ClientService clientService, HairdresserService hairdresserService,
      BeautyServiceService beautyServiceService, AppointmentService appointmentService,
      PasswordEncoder passwordEncoder, RoleRepository roleRepository,
      TimeSlotService timeSlotService) {
    this.clientService = clientService;
    this.hairdresserService = hairdresserService;
    this.beautyServiceService = beautyServiceService;
    this.appointmentService = appointmentService;
    this.passwordEncoder = passwordEncoder;
    this.roleRepository = roleRepository;
    this.timeSlotService = timeSlotService;
  }

  @PostMapping("/register")
  public String processRegistrationForm(@ModelAttribute("client") Client client) {
    LOGGER.info("Registration form processing for a client.");
    client.setPassword(passwordEncoder.encode(client.getPassword()));
    client.setRoles(Set.of(roleRepository.findByName("ROLE_CLIENT").orElseThrow()));
    clientService.saveClient(client);

    return "redirect:/client/appointments";
  }

  @GetMapping("/client/book")
  public String showBookingPage(Model model) {
    LOGGER.info("Booking page is open.");
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
    LOGGER.info("Booking an appointment.");

    if (dateTime.isEmpty()) {
      return "redirect:/client/book";
    }

    String[] dateTimeParts = dateTime.split(" - ");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeParts[0], formatter);

    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
    BeautyService beautyService = beautyServiceService.findFirstByName(serviceName).orElseThrow(
        () -> new ResourceNotFoundException("Service with name " + serviceName + " not found"));
    String userEmail = authentication.getName();

    Client client = clientService.findClientByEmail(userEmail).orElseThrow(
        () -> new UsernameNotFoundException("Client not found with email: " + userEmail));

    Appointment appointment = new Appointment();
    appointment.setClient(client);
    appointment.setHairdresser(hairdresser);
    appointment.setBeautyService(beautyService);
    appointment.setAppointmentTime(appointmentDateTime);
    appointment.setPaymentStatus(PaymentStatus.PENDING);

    appointmentService.saveAppointment(appointment);

    return "redirect:/client/appointments";
  }

  @GetMapping("/client/timeslots")
  public ResponseEntity<List<String>> getTimeSlots(
      @RequestParam("hairdresserId") Long hairdresserId,
      @RequestParam("serviceName") String serviceName) {

    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

    List<TimeSlot> timeSlots = timeSlotService.generateTimeSlots(hairdresser);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    List<String> timeSlotStrings = timeSlots.stream()
        .map(slot -> formatter.format(slot.getStart()) + " - " + formatter.format(slot.getEnd()))
        .collect(Collectors.toList());

    return ResponseEntity.ok(timeSlotStrings);
  }


  @GetMapping("/client/appointments")
  public String viewAppointments(Authentication authentication, Model model) {
    LOGGER.info("Client viewing appointments.");
    if (authentication == null || authentication.getName() == null) {
      return "redirect:/login";
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Client client = clientService.findClientByEmail(userDetails.getUsername()).orElseThrow(
        () -> new UsernameNotFoundException(
            "Client not found with email: " + userDetails.getUsername()));

    List<Appointment> appointments = appointmentService.findByClient(client);
    model.addAttribute("appointments", appointments);
    return "client/appointments";
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    LOGGER.info("Registration form processing for a client.");
    model.addAttribute("client", new Client());
    return "register";
  }

  @GetMapping("/login")
  public String showLoginPage() {
    return "login";
  }
}