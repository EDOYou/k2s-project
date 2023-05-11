package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.exceptions.RoleNotFoundException;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
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
  private RoleService roleService;

  @Autowired
  public AdminController(AppointmentService appointmentService,
      HairdresserService hairdresserService, RoleService roleService) {
    this.appointmentService = appointmentService;
    this.hairdresserService = hairdresserService;
    this.roleService = roleService;
  }

  @GetMapping("/dashboard")
  public String adminDashboard(Model model) {
    System.out.println("Inside /admin/dashboard controller");
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
    appointmentService.deleteAppointment(appointmentId);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/approveHairdresser/{userId}")
  public String approveHairdresser(@PathVariable("userId") Long hairdresserId) {
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

    Role hairdresserRole = roleService.getRoleByName("ROLE_HAIRDRESSER")
        .orElseThrow(() -> new RoleNotFoundException("Role not found."));

    hairdresser.getRoles().removeIf(role -> role.getName().equals("ROLE_APPLICANT"));
    hairdresser.getRoles().add(hairdresserRole);

    hairdresserService.saveHairdresser(hairdresser);
    // TODO: Send notification to hairdresser about approval

    return "redirect:/admin/dashboard";
  }

  @GetMapping("/hairdresserRegistrationReview")
  public String reviewHairdresserRegistration(Model model) {
    // Retrieve the hairdresser registration details from the temporary storage or queue
    Hairdresser hairdresser = hairdresserService.getTemporaryRegistration();

    // Pass the hairdresser registration details to the admin's review page
    model.addAttribute("hairdresser", hairdresser);

    return "admin/hairdresserRegistrationReview";
  }
}