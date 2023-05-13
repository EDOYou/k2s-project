package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.exceptions.BeautyServiceNotFoundException;
import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class GuestServiceFacade {

  private static final Logger LOGGER = Logger.getLogger(GuestServiceFacade.class.getName());

  private final PasswordEncoder passwordEncoder;
  private final BeautyServiceService beautyServiceService;
  private final HairdresserService hairdresserService;
  private final RoleService roleService;

  public GuestServiceFacade(PasswordEncoder passwordEncoder,
      BeautyServiceService beautyServiceService,
      HairdresserService hairdresserService,
      RoleService roleService) {
    this.passwordEncoder = passwordEncoder;
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
    this.roleService = roleService;
  }

  public ServicesData getServicesData(Long hairdresserId, Long serviceId, String sortBy) {
    LOGGER.info("Guest viewing the services.");

    List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs = new ArrayList<>();
    List<BeautyService> services;
    List<Hairdresser> hairdressers;

    if (hairdresserId != null) {
      Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
      hairdresser.getBeautyServices().forEach(service -> serviceHairdresserPairs.add(Pair.of(service, hairdresser)));
      services = new ArrayList<>(hairdresser.getBeautyServices());
      hairdressers = Collections.singletonList(hairdresser);
    } else if (serviceId != null) {
      BeautyService service = beautyServiceService.findById(serviceId)
          .orElseThrow(() -> new BeautyServiceNotFoundException("Service not found."));
      service.getHairdressers().forEach(hairdresser -> serviceHairdresserPairs.add(Pair.of(service, hairdresser)));
      services = Collections.singletonList(service);
      hairdressers = new ArrayList<>(service.getHairdressers());
    } else {
      services = beautyServiceService.findAll();
      hairdressers = hairdresserService.findAllHairdressers(sortBy);
      for (BeautyService service : services) {
        for (Hairdresser hairdresser : service.getHairdressers()) {
          if (hairdresser.isApproved()) {
            serviceHairdresserPairs.add(Pair.of(service, hairdresser));
          }
        }
      }
      serviceHairdresserPairs.sort(getServiceComparator(sortBy));
    }

    LOGGER.info("Hairdressers: " + hairdressers);
    LOGGER.info("Services: " + services);

    return new ServicesData(serviceHairdresserPairs, services, hairdressers, sortBy, hairdresserId, serviceId);
  }

  public void registerHairdresser(Hairdresser hairdresser) {
    hairdresser.setPassword(passwordEncoder.encode(hairdresser.getPassword()));
    hairdresser.setRoles(Set.of(roleService.getRoleByName("ROLE_HAIRDRESSER").orElseThrow()));

    List<BeautyService> services = beautyServiceService.findAllByIdIn(
        hairdresser.getSelectedServiceIds());
    hairdresser.setBeautyServices(new HashSet<>(services));
  }

  private Comparator<Pair<BeautyService, Hairdresser>> getServiceComparator(String sortBy) {
    if ("serviceName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(pair -> pair.getFirst().getName());
    } else if ("lastName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(pair -> pair.getSecond().getLastName());
    } else if ("rating".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing((Pair<BeautyService, Hairdresser> pair) -> pair.getSecond().getRating())
          .reversed();
    } else {
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
  }
}