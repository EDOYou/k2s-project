package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.AppointmentNotFoundException;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.ClientService;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.implementations.appointment_details.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceFacade {

  private final ClientService clientService;
  private final HairdresserService hairdresserService;
  private final BeautyServiceService beautyServiceService;
  private final AppointmentService appointmentService;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final TimeSlotService timeSlotService;
  private final FeedbackService feedbackService;

  @Autowired
  public ClientServiceFacade(ClientService clientService, HairdresserService hairdresserService,
      BeautyServiceService beautyServiceService, AppointmentService appointmentService,
      PasswordEncoder passwordEncoder, RoleRepository roleRepository,
      TimeSlotService timeSlotService, FeedbackService feedbackService) {
    this.clientService = clientService;
    this.hairdresserService = hairdresserService;
    this.beautyServiceService = beautyServiceService;
    this.appointmentService = appointmentService;
    this.passwordEncoder = passwordEncoder;
    this.roleRepository = roleRepository;
    this.timeSlotService = timeSlotService;
    this.feedbackService = feedbackService;
  }

  public void processRegistrationForm(Client client) {
    client.setPassword(passwordEncoder.encode(client.getPassword()));
    client.setRoles(Set.of(roleRepository.findByName("ROLE_CLIENT").orElseThrow()));
    clientService.saveClient(client);
  }

  public List<Hairdresser> getHairdressersWithServices() {
    return hairdresserService.findAllWithBeautyServices();
  }

  public List<String> getDistinctServiceNames() {
    return beautyServiceService.findDistinctServiceNames();
  }

  public void bookAppointment(Authentication authentication, Long hairdresserId,
      String serviceName, String dateTime) {
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
  }

  public List<String> getTimeSlots(Long hairdresserId) {
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

    List<TimeSlot> timeSlots = timeSlotService.generateTimeSlots(hairdresser);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    return timeSlots.stream()
        .map(slot -> formatter.format(slot.getStart()) + " - " + formatter.format(slot.getEnd()))
        .collect(Collectors.toList());
  }

  public List<Appointment> getClientAppointments(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      throw new IllegalArgumentException("Authentication is required.");
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Client client = clientService.findClientByEmail(userDetails.getUsername()).orElseThrow(
        () -> new UsernameNotFoundException(
            "Client not found with email: " + userDetails.getUsername()));

    return appointmentService.findByClient(client);
  }

  @Transactional
  public void saveFeedback(Authentication authentication, Long appointmentId, Feedback feedback) {

    Appointment appointment = appointmentService.findById(appointmentId)
        .orElseThrow(
            () -> new AppointmentNotFoundException("No appointment found with the provided ID."));

    feedback.setAppointment(appointment);

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Client client = clientService.findClientByEmail(userDetails.getUsername()).orElseThrow(
        () -> new UsernameNotFoundException(
            "Client not found with email: " + userDetails.getUsername()));
    feedback.setClient(client);
    feedback.setCreatedAt(LocalDateTime.now());
    feedbackService.createFeedback(feedback);
  }

}