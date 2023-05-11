package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
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

  @Autowired
  public AdminController(AppointmentService appointmentService) {
    this.appointmentService = appointmentService;
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
}