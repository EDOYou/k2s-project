package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.facade.GuestServiceFacade;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <code>GuestController</code> is a Spring controller for handling guest user
 * interactions, such as viewing available beauty services and hairdressers.
 *
 * @see BeautyServiceService
 * @see HairdresserService
 */
@Controller
public class GuestController {
  private final BeautyServiceService beautyServiceService;
  private final GuestServiceFacade guestServiceFacade;

  /**
   * Constructs a <code>GuestController</code> instance with the provided beauty service and
   * hairdresser service.
   *
   * @param beautyServiceService the beauty service.
   */
  public GuestController(
      BeautyServiceService beautyServiceService,
      GuestServiceFacade guestServiceFacade) {
    this.beautyServiceService = beautyServiceService;
    this.guestServiceFacade = guestServiceFacade;
  }

  @GetMapping("/guest/services")
  public String viewServices(Model model,
      @RequestParam(name = "hairdresser", required = false) Long hairdresserId,
      @RequestParam(name = "service", required = false) Long serviceId,
      @RequestParam(name = "sort", defaultValue = "lastName") String sortBy) {
    ServicesData servicesData = guestServiceFacade.getServicesData(hairdresserId, serviceId, sortBy);

    model.addAttribute("serviceHairdresserPairs", servicesData.getServiceHairdresserPairs());
    model.addAttribute("services", servicesData.getServices());
    model.addAttribute("hairdressers", servicesData.getHairdressers());
    model.addAttribute("sort", servicesData.getSortBy());
    model.addAttribute("selectedHairdresser", servicesData.getSelectedHairdresser());
    model.addAttribute("selectedService", servicesData.getSelectedService());
    return "/guest/services";
  }

  @PostMapping("/hairdresser/register_hairdresser")
  @Transactional
  public String processRegistrationForm(@ModelAttribute("hairdresser") Hairdresser hairdresser) {
    guestServiceFacade.registerHairdresser(hairdresser);
    return "redirect:home";
  }

  @GetMapping("/hairdresser/register_hairdresser")
  public String showRegistrationForm(Model model) {
    List<BeautyService> services = beautyServiceService.findAll();
    model.addAttribute("services", services);
    model.addAttribute("hairdresser", new Hairdresser());
    return "/hairdresser/register_hairdresser";
  }
}