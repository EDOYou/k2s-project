package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.exceptions.BeautyServiceNotFoundException;
import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GuestServiceFacade {

  private static final Logger LOGGER = LogManager.getLogger(GuestServiceFacade.class.getName());

  private final BeautyServiceService beautyServiceService;
  private final HairdresserService hairdresserService;

  public GuestServiceFacade(BeautyServiceService beautyServiceService,
      HairdresserService hairdresserService) {
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
  }

  public ServicesData getServicesData(Long hairdresserId, Long serviceId, String sortBy) {
    LOGGER.info("Guest viewing the services ...");

    if (hairdresserId != null) {
      LOGGER.info("Getting services data for HAIRDRESSER ...");
      return getServicesDataForHairdresser(hairdresserId);
    } else if (serviceId != null) {
      LOGGER.info("Getting services data for SERVICE ...");
      return getServicesDataForService(serviceId, sortBy);
    } else {
      LOGGER.info("Getting services data for ALL ...");
      return getServicesDataForAll(sortBy);
    }
  }

  private ServicesData getServicesDataForAll(String sortBy) {
    var services = beautyServiceService.findAll();
    var hairdressers = hairdresserService.findAllHairdressers(sortBy);

    List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs = new ArrayList<>();
    for (BeautyService service : services) {
      for (Hairdresser hairdresser : service.getHairdressers()) {
        if (hairdresser.isApproved()) {
          serviceHairdresserPairs.add(Pair.of(service, hairdresser));
        }
      }
    }

    serviceHairdresserPairs.sort(getServiceComparator(sortBy));
    return new ServicesData(serviceHairdresserPairs, services, hairdressers, sortBy, null, null);
  }

  private ServicesData getServicesDataForHairdresser(Long hairdresserId) {
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
    Set<BeautyService> beautyServices = hairdresser.getBeautyServices();

    List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs = new ArrayList<>();
    beautyServices.forEach(service -> serviceHairdresserPairs.add(Pair.of(service, hairdresser)));

    return new ServicesData(serviceHairdresserPairs, new ArrayList<>(beautyServices),
        Collections.singletonList(hairdresser), null, hairdresserId, null);
  }

  private ServicesData getServicesDataForService(Long serviceId, String sortBy) {
    BeautyService service = beautyServiceService.findById(serviceId)
        .orElseThrow(() -> new BeautyServiceNotFoundException("Service not found."));
    Set<Hairdresser> hairdressers = service.getHairdressers();

    List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs = new ArrayList<>();
    hairdressers.forEach(hairdresser -> serviceHairdresserPairs.add(Pair.of(service, hairdresser)));

    return new ServicesData(serviceHairdresserPairs, Collections.singletonList(service),
        new ArrayList<>(hairdressers), sortBy, null, serviceId);
  }

  private Comparator<Pair<BeautyService, Hairdresser>> getServiceComparator(String sortBy) {
    if ("serviceName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(pair -> pair.getFirst().getName());
    } else if ("lastName".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(pair -> pair.getSecond().getLastName());
    } else if ("rating".equalsIgnoreCase(sortBy)) {
      return Comparator.comparing(
              (Pair<BeautyService, Hairdresser> pair) -> pair.getSecond().getRating())
          .reversed();
    } else {
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
  }
}