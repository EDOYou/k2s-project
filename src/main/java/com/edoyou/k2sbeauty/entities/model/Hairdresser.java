package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "hairdressers")
public class Hairdresser extends User {

  @Column(nullable = false)
  private String specialization;

  @Column(nullable = false)
  private double rating;

  @Column(name = "is_approved", nullable = false)
  private boolean isApproved = false;

  @OneToMany(mappedBy = "hairdresser")
  private Set<BeautyService> beautyServices = new HashSet<>();

  @OneToMany(mappedBy = "hairdresser")
  private Set<Appointment> appointments = new HashSet<>();

  public String getBeautyServicesNamesString() {
    return beautyServices.stream()
        .map(BeautyService::getName)
        .collect(Collectors.joining(",")).replaceAll("\\s+", "");
  }

  public List<String> getBeautyServicesNames() {
    return beautyServices.stream().map(BeautyService::getName).collect(Collectors.toList());
  }

  public boolean isApproved() {
    return isApproved;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public void setAppointments(Set<Appointment> appointments) {
    this.appointments = appointments;
  }

  public void setBeautyServices(Set<BeautyService> beautyServices) {
    this.beautyServices = beautyServices;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public void setApproved(boolean approved) {
    isApproved = approved;
  }

  public String getSpecialization() {
    return specialization;
  }

  public Set<Appointment> getAppointments() {
    return appointments;
  }

  public Set<BeautyService> getBeautyServices() {
    return beautyServices;
  }

  public double getRating() {
    return rating;
  }
}