package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.facade.AdminServiceFacade;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final AdminServiceFacade adminServiceFacade;

  @Autowired
  public AdminController(AdminServiceFacade adminServiceFacade) {
    this.adminServiceFacade = adminServiceFacade;
  }

  @GetMapping("/dashboard")
  public String adminDashboard(Model model) {
    List<Appointment> appointments = adminServiceFacade.findAllAppointments();
    model.addAttribute("appointments", appointments);
    return "admin/dashboard";
  }

  @PostMapping("/changeTimeSlot/{appointmentId}")
  public String changeTimeSlot(@PathVariable("appointmentId") Long appointmentId,
      @RequestParam("newTimeSlot") String newTimeSlot) {
    adminServiceFacade.changeTimeSlot(appointmentId, newTimeSlot);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/acceptPayment/{appointmentId}")
  public String acceptPayment(@PathVariable("appointmentId") Long appointmentId) {
    adminServiceFacade.updatePaymentStatus(appointmentId);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/cancelAppointment/{appointmentId}")
  public String cancelAppointment(@PathVariable("appointmentId") Long appointmentId) {
    adminServiceFacade.cancelAppointment(appointmentId);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/approveHairdresser/{userId}")
  public String approveHairdresser(@PathVariable("userId") Long hairdresserId) {
    adminServiceFacade.approveHairdresser(hairdresserId);
    return "redirect:/admin/dashboard";
  }

  @PostMapping("/rejectHairdresser/{userId}")
  public String rejectHairdresser(@PathVariable("userId") Long hairdresserId) {
    adminServiceFacade.rejectHairdresser(hairdresserId);
    return "redirect:/admin/dashboard";
  }

  @GetMapping("/hairdresserRegistrationReview")
  public String reviewHairdresserRegistration(Model model) {
    List<Hairdresser> hairdressers = adminServiceFacade.findAllYetNotApproved(false);
    model.addAttribute("hairdressers", hairdressers);
    return "admin/hairdresserRegistrationReview";
  }

  @GetMapping("/add_service")
  public String showAddServiceForm(Model model) {
    model.addAttribute("service", new BeautyService());
    return "admin/add_service";
  }

  @PostMapping("/add_service")
  public String handleAddService(@ModelAttribute("service") BeautyService service) {
    adminServiceFacade.saveService(service);
    return "redirect:/admin/services";
  }

  @GetMapping("/services")
  public String showServices(Model model) {
    List<BeautyService> services = adminServiceFacade.findAllBeautyServices();
    model.addAttribute("services", services);
    return "admin/services";
  }

  @GetMapping("/assign_service")
  public String showAssignServiceForm(Model model) {
    List<BeautyService> services = adminServiceFacade.findAllBeautyServices();
    List<Hairdresser> hairdressers = adminServiceFacade.findAllHairdressers();
    model.addAttribute("services", services);
    model.addAttribute("hairdressers", hairdressers);
    return "/admin/assign_service";
  }

  @PostMapping("/assign_service")
  public String handleAssignService(@ModelAttribute("hairdresserId") Long hairdresserId,
      @RequestParam("serviceId") Long serviceId) {
    adminServiceFacade.assignServiceToHairdresser(hairdresserId, serviceId);
    return "redirect:/admin/assign_service";
  }

  @GetMapping("/hairdressers")
  public String showHairdressers(Model model) {
    List<Hairdresser> hairdressers = adminServiceFacade.findHairdressersWithServices();
    model.addAttribute("hairdressers", hairdressers);
    return "admin/hairdressers";
  }

  @GetMapping("/changeLanguage")
  public String changeLanguage(HttpServletRequest request, @RequestParam String lang) {
    Locale locale = Locale.forLanguageTag(lang);
    request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
    return "redirect:" + request.getHeader("referer");
  }

}