package com.edoyou.k2sbeauty.pojo;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import java.util.List;
import org.springframework.data.util.Pair;

public class ServicesData {
  private List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs;
  private List<BeautyService> services;
  private List<Hairdresser> hairdressers;
  private String sortBy;
  private Long selectedHairdresser;
  private Long selectedService;

  public ServicesData(List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs,
      List<BeautyService> services, List<Hairdresser> hairdressers, String sortBy,
      Long selectedHairdresser, Long selectedService) {
    this.serviceHairdresserPairs = serviceHairdresserPairs;
    this.services = services;
    this.hairdressers = hairdressers;
    this.sortBy = sortBy;
    this.selectedHairdresser = selectedHairdresser;
    this.selectedService = selectedService;
  }

  public List<Pair<BeautyService, Hairdresser>> getServiceHairdresserPairs() {
    return serviceHairdresserPairs;
  }

  public void setServiceHairdresserPairs(
      List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs) {
    this.serviceHairdresserPairs = serviceHairdresserPairs;
  }

  public List<BeautyService> getServices() {
    return services;
  }

  public void setServices(List<BeautyService> services) {
    this.services = services;
  }

  public List<Hairdresser> getHairdressers() {
    return hairdressers;
  }

  public void setHairdressers(List<Hairdresser> hairdressers) {
    this.hairdressers = hairdressers;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public Long getSelectedHairdresser() {
    return selectedHairdresser;
  }

  public void setSelectedHairdresser(Long selectedHairdresser) {
    this.selectedHairdresser = selectedHairdresser;
  }

  public Long getSelectedService() {
    return selectedService;
  }

  public void setSelectedService(Long selectedService) {
    this.selectedService = selectedService;
  }
}
