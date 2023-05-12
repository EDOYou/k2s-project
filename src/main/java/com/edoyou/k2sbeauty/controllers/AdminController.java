package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.exceptions.RoleNotFoundException;
import com.edoyou.k2sbeauty.services.implementations.NotificationService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import jakarta.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());
  private final AppointmentService appointmentService;
  private final HairdresserService hairdresserService;
  private final RoleService roleService;
  private final NotificationService notificationService;

  @Autowired
  public AdminController(AppointmentService appointmentService,
      HairdresserService hairdresserService, RoleService roleService, NotificationService notificationService) {
    this.appointmentService = appointmentService;
    this.hairdresserService = hairdresserService;
    this.roleService = roleService;
    this.notificationService = notificationService;
  }

  @GetMapping("/dashboard")
  public String adminDashboard(Model model) {
    LOGGER.info("Inside /admin/dashboard controller");
    List<Appointment> appointments = appointmentService.findAllAppointments();
    LOGGER.info("Appointments for admin dashboard: " + appointments);
    model.addAttribute("appointments", appointments);
    return "admin/dashboard";
  }

  @PostMapping("/changeTimeSlot/{appointmentId}")
  public String changeTimeSlot(@PathVariable("appointmentId") Long appointmentId,
      @RequestParam("newTimeSlot") String newTimeSlot) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime newAppointmentTime = LocalDateTime.parse(newTimeSlot, formatter);
    Appointment appointment = appointmentService.findById(appointmentId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Appointment not found for this id :: " + appointmentId));

    appointment.setAppointmentTime(newAppointmentTime);
    appointmentService.updateAppointment(appointmentId, appointment);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/acceptPayment/{appointmentId}")
  public String acceptPayment(@PathVariable("appointmentId") Long appointmentId) {
    PaymentStatus status = PaymentStatus.fromString("PAID", PaymentStatus.PENDING);
    appointmentService.updatePaymentStatus(appointmentId, status);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/updatePaymentStatus/{appointmentId}")
  public String updatePaymentStatus(@PathVariable("appointmentId") Long appointmentId,
      @RequestParam("newStatus") String newStatus) {
    PaymentStatus status = PaymentStatus.fromString(newStatus, PaymentStatus.PENDING);
    appointmentService.updatePaymentStatus(appointmentId, status);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/cancelAppointment/{appointmentId}")
  public String cancelAppointment(@PathVariable("appointmentId") Long appointmentId) {
    appointmentService.deleteAppointment(appointmentId, "ROLE_ADMIN");
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/approveHairdresser/{userId}")
  @Transactional
  public String approveHairdresser(@PathVariable("userId") Long hairdresserId) {
    LOGGER.info("Admin approves hairdresser.");
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

    if (hairdresser == null) {
      throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
    }

    Role hairdresserRole = roleService.getRoleByName("ROLE_HAIRDRESSER")
        .orElseThrow(() -> new RoleNotFoundException("Role not found."));

    hairdresser.getRoles().removeIf(role -> role.getName().equals("ROLE_APPLICANT"));
    hairdresser.getRoles().add(hairdresserRole);
    hairdresser.setApproved(true);

    hairdresserService.saveHairdresser(hairdresser);
    String message = "Dear " + hairdresser.getFirstName() + ", your application has been approved. Welcome to our team!";
    notificationService.sendNotification(hairdresser.getEmail(), "Application Approved", message);

    return "redirect:/admin/dashboard";
  }

  @PostMapping("/rejectHairdresser/{userId}")
  @Transactional
  public String rejectHairdresser(@PathVariable("userId") Long hairdresserId) {
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

    if (hairdresser == null) {
      throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
    }
    // Send notification to the hairdresser about the rejection
    String message = "Dear " + hairdresser.getFirstName() + ", your application has been reviewed and unfortunately, you were not accepted at this time. Please consider applying again in the future.";
    notificationService.sendNotification(hairdresser.getEmail(), "Application Rejected", message);

    // Delete the hairdresser's application
    hairdresserService.deleteHairdresser(hairdresserId);

    return "redirect:/admin/dashboard";
  }

  @GetMapping("/hairdresserRegistrationReview")
  public String reviewHairdresserRegistration(Model model) {
    List<Hairdresser> hairdressers = hairdresserService.findAllHairdressersByApprovalStatus(false);

    // Pass the hairdresser registration details to the admin's review page
    model.addAttribute("hairdressers", hairdressers);

    return "admin/hairdresserRegistrationReview";
  }
}