package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.facade.GuestServiceFacade;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

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

  private final GuestServiceFacade guestServiceFacade;

  /**
   * Constructs a <code>GuestController</code> instance with the provided beauty service and
   * hairdresser service.
   **/
  public GuestController(
      GuestServiceFacade guestServiceFacade) {
    this.guestServiceFacade = guestServiceFacade;
  }

  @GetMapping("/services")
  public String viewServices(Model model,
      @RequestParam(name = "hairdresser", required = false) Long hairdresserId,
      @RequestParam(name = "service", required = false) Long serviceId,
      @RequestParam(name = "sort", defaultValue = "lastName") String sortBy) {
    ServicesData servicesData = guestServiceFacade.getServicesData(hairdresserId, serviceId,
        sortBy);

    model.addAttribute("serviceHairdresserPairs", servicesData.getServiceHairdresserPairs());
    model.addAttribute("services", servicesData.getServices());
    model.addAttribute("hairdressers", servicesData.getHairdressers());
    model.addAttribute("sort", servicesData.getSortBy());
    model.addAttribute("selectedHairdresser", servicesData.getSelectedHairdresser());
    model.addAttribute("selectedService", servicesData.getSelectedService());
    return "/guest/services";
  }

  @GetMapping("/changeLanguage")
  public String changeLanguage(HttpServletRequest request, @RequestParam String lang) {
    Locale locale = Locale.forLanguageTag(lang);
    request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
    return "redirect:" + request.getHeader("referer");
  }
}