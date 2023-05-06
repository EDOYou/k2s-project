package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <code>GuestController</code> is a Spring controller for handling guest user
 * interactions, such as viewing available beauty services and hairdressers.
 *
 * @see BeautyServiceService
 * @see HairdresserService
 */
@Controller
@RequestMapping("/guest")
public class GuestController {

  private final BeautyServiceService beautyServiceService;
  private final HairdresserService hairdresserService;

  /**
   * Constructs a <code>GuestController</code> instance with the provided beauty
   * service and hairdresser service.
   *
   * @param beautyServiceService the beauty service.
   * @param hairdresserService   the hairdresser service.
   */
  public GuestController(BeautyServiceService beautyServiceService, HairdresserService hairdresserService) {
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
  }

  /**
   * Handles the request to view available beauty services and hairdressers.
   * Fetches the list of beauty services and hairdressers, and adds them along
   * with the optional query parameters to the model.
   *
   * @param model         the model to add attributes to
   * @param hairdresserId the optional hairdresser ID query parameter
   * @param serviceId     the optional service ID query parameter
   * @param sortBy        the optional sort by query parameter, defaults to "lastName"
   * @return the name of the view to render
   */
  @GetMapping("/services")
  public String viewServices(Model model,
      @RequestParam(name = "hairdresser", required = false) Long hairdresserId,
      @RequestParam(name = "service", required = false) Long serviceId,
      @RequestParam(name = "sort", defaultValue = "lastName") String sortBy) {

    List<BeautyService> services = beautyServiceService.findAll();
    List<Hairdresser> hairdressers = hairdresserService.findAllHairdressers(sortBy);

    model.addAttribute("services", services);
    model.addAttribute("hairdressers", hairdressers);
    model.addAttribute("selectedHairdresser", hairdresserId);
    model.addAttribute("selectedService", serviceId);
    model.addAttribute("sort", sortBy);

    return "/guest/services";
  }
}