package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.Comparator;
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
   * Constructs a <code>GuestController</code> instance with the provided beauty service and
   * hairdresser service.
   *
   * @param beautyServiceService the beauty service.
   * @param hairdresserService   the hairdresser service.
   */
  public GuestController(BeautyServiceService beautyServiceService,
      HairdresserService hairdresserService) {
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
  }

  @GetMapping("/services")
  public String viewServices(Model model,
      @RequestParam(name = "hairdresser", required = false) Long hairdresserId,
      @RequestParam(name = "service", required = false) Long serviceId,
      @RequestParam(name = "sort", defaultValue = "lastName") String sortBy) {

    List<BeautyService> services = beautyServiceService.findAll();
    List<Hairdresser> hairdressers = hairdresserService.findAllHairdressers(sortBy);

    List<BeautyService> filteredServices = services.stream()
        .filter(service -> service.getHairdresser() != null)
        .filter(service -> hairdresserId == null || service.getHairdresser().getId()
            .equals(hairdresserId))
        .filter(service -> serviceId == null || service.getId().equals(serviceId))
        .sorted(getServiceComparator(sortBy))
        .toList();

    model.addAttribute("services", services);
    model.addAttribute("hairdressers", hairdressers);
    model.addAttribute("filteredServices", filteredServices);
    model.addAttribute("selectedHairdresser", hairdresserId);
    model.addAttribute("selectedService", serviceId);
    model.addAttribute("sort", sortBy);

    return "/guest/services";
  }

  private Comparator<BeautyService> getServiceComparator(String sortBy) {
    if ("serviceName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(BeautyService::getName);
    } else if ("lastName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(
          (BeautyService service) -> service.getHairdresser().getLastName());
    } else if ("rating".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing((BeautyService service) -> service.getHairdresser().getRating())
          .reversed();
    } else {
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
  }
}