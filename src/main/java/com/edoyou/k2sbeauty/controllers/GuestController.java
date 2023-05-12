package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.exceptions.RoleNotFoundException;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import com.edoyou.k2sbeauty.services.interfaces.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  private static final Logger LOGGER = Logger.getLogger(GuestController.class.getName());

  private final PasswordEncoder passwordEncoder;
  private final BeautyServiceService beautyServiceService;
  private final HairdresserService hairdresserService;
  private final RoleService roleService;
  private final UserService userService;

  /**
   * Constructs a <code>GuestController</code> instance with the provided beauty service and
   * hairdresser service.
   *
   * @param beautyServiceService the beauty service.
   * @param hairdresserService   the hairdresser service.
   */
  public GuestController(PasswordEncoder passwordEncoder,
      BeautyServiceService beautyServiceService,
      HairdresserService hairdresserService,
      RoleService roleService,
      @Qualifier("userServiceImpl") UserService userService) {
    this.passwordEncoder = passwordEncoder;
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
    this.roleService = roleService;
    this.userService = userService;
  }

  @GetMapping("/guest/services")
  public String viewServices(Model model,
      @RequestParam(name = "hairdresser", required = false) Long hairdresserId,
      @RequestParam(name = "service", required = false) Long serviceId,
      @RequestParam(name = "sort", defaultValue = "lastName") String sortBy) {
    LOGGER.info("Guest viewing his appointments.");

    List<BeautyService> services = beautyServiceService.findAll();
    List<Hairdresser> hairdressers = hairdresserService.findAllHairdressers(sortBy);

//    List<BeautyService> filteredServices = services.stream()
//        .filter(service -> service.getHairdresser() != null && service.getHairdresser().isApproved())
//        .filter(service -> hairdresserId == null || service.getHairdresser().getId()
//            .equals(hairdresserId))
//        .filter(service -> serviceId == null || service.getId().equals(serviceId))
//        .sorted(getServiceComparator(sortBy))
//        .toList();

    if (hairdresserId != null) {
      Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
      services = new ArrayList<>(hairdresser.getBeautyServices());
    } else if (serviceId != null) {
      BeautyService service = beautyServiceService.findById(serviceId).orElseThrow();
      hairdressers = new ArrayList<>(service.getHairdressers());
      services = Collections.singletonList(service);
    } else {
      services = beautyServiceService.findAll();
    }

    model.addAttribute("services", services);
    model.addAttribute("hairdressers", hairdressers);
    //model.addAttribute("filteredServices", filteredServices);
    //model.addAttribute("selectedHairdresser", hairdresserId);
    //model.addAttribute("selectedService", serviceId);
    model.addAttribute("sort", sortBy);

    return "/guest/services";
  }

  @PostMapping("/guest/applyHairdresser")
  public String applyHairdresser(@ModelAttribute Hairdresser hairdresser) {
    LOGGER.info("Application for hairdresser started.");

    Optional<User> existingUserOptional = userService.findUserByEmail(hairdresser.getEmail());
    if (existingUserOptional.isPresent()) {
      LOGGER.info("E-mail is present.");
      User existingUser = existingUserOptional.get();

      Role applicantRole = roleService.getRoleByName("ROLE_APPLICANT")
          .orElseThrow(() -> new RoleNotFoundException("Role not found."));

      existingUser.getRoles().add(applicantRole);
      userService.saveUser(existingUser);
    } else {
      LOGGER.info("E-mail is NOT present.");
      Role applicantRole = roleService.getRoleByName("ROLE_APPLICANT")
          .orElseThrow(() -> new RoleNotFoundException("Role not found."));

      hairdresser.getRoles().add(applicantRole);
      hairdresser.setRating(0.0);
      userService.saveUser(hairdresser);
    }

    return "redirect:/home";
  }

  @PostMapping("/hairdresser/register_hairdresser")
  @Transactional
  public String processRegistrationForm(@ModelAttribute("hairdresser") Hairdresser hairdresser) {
    LOGGER.info("Processing registration form for hairdresser.");
    hairdresser.setPassword(passwordEncoder.encode(hairdresser.getPassword()));
    hairdresser.setRoles(Set.of(roleService.getRoleByName("ROLE_HAIRDRESSER").orElseThrow()));

    List<BeautyService> services = beautyServiceService.findAllByIdIn(
        hairdresser.getSelectedServiceIds());
    hairdresser.setBeautyServices(new HashSet<>(services));

    LOGGER.info("Hairdresser " + hairdresser);
    hairdresserService.saveHairdresser(hairdresser);

    // fetch the hairdresser from the database
    Hairdresser savedHairdresser = hairdresserService.findById(hairdresser.getId());

    // print out the services associated with the hairdresser
    LOGGER.info("Services for hairdresser " + savedHairdresser.getFirstName() + " "
        + savedHairdresser.getLastName() + ":");
    for (BeautyService service : savedHairdresser.getBeautyServices()) {
      LOGGER.info(service.getName());
    }

    return "redirect:home";
  }

  @GetMapping("/hairdresser/register_hairdresser")
  public String showRegistrationForm(Model model) {
    // Adding the available services to the model
    List<BeautyService> services = beautyServiceService.findAll();
    model.addAttribute("services", services);
    model.addAttribute("hairdresser", new Hairdresser());
    return "/hairdresser/register_hairdresser";
  }

  private Comparator<BeautyService> getServiceComparator(String sortBy) {
    if ("serviceName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(BeautyService::getName);
    } else if ("lastName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(service -> service.getHairdressers().stream()
          .findFirst()
          .map(Hairdresser::getLastName)
          .orElse(""));
    } else if ("rating".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing((BeautyService service) -> service.getHairdressers().stream()
              .findFirst()
              .map(Hairdresser::getRating)
              .orElse(0.0))
          .reversed();
    } else {
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
  }

}