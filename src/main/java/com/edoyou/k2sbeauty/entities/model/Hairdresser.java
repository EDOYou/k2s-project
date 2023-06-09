package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Hairdresser entity class represents a hairdresser in the BeautySalon application.
 * It extends the {@link User} class and adds the specific details related to a hairdresser.
 */
@Entity
@Table(name = "hairdressers")
public class Hairdresser extends User {

  @Column(nullable = false)
  private String specialization;

  @Column(nullable = false)
  private double rating;

  @Column(name = "is_approved", nullable = false)
  private boolean isApproved = false;

  // Using this to carry data from the form to the controller
  @Transient
  private List<Long> selectedServiceIds;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(
      name = "hairdresser_working_hours",
      joinColumns = @JoinColumn(name = "hairdresser_id"),
      inverseJoinColumns = @JoinColumn(name = "working_hours_id")
  )
  private Set<WorkingHours> workingHours = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "hairdresser_service",
      joinColumns = @JoinColumn(name = "hairdresser_id"),
      inverseJoinColumns = @JoinColumn(name = "service_id"))
  private Set<BeautyService> beautyServices = new HashSet<>();

  @OneToMany(mappedBy = "hairdresser")
  private Set<Appointment> appointments = new HashSet<>();

  public Optional<WorkingHours> getWorkingHoursForDay(DayOfWeek dayOfWeek) {
    return this.workingHours.stream()
        .filter(wh -> wh.getDayOfWeek().equals(dayOfWeek))
        .findFirst();
  }


  public Set<WorkingHours> getWorkingHours() {
    return workingHours;
  }

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

  public void setRating(double rating) {
    this.rating = rating;
  }

  public void setApproved(boolean approved) {
    isApproved = approved;
  }

  public void setSelectedServiceIds(List<Long> selectedServiceIds) {
    this.selectedServiceIds = selectedServiceIds;
  }

  public void setWorkingHours(Set<WorkingHours> workingHours) {
    this.workingHours = workingHours;
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

  public List<Long> getSelectedServiceIds() {
    return selectedServiceIds;
  }


  @Override
  public String toString() {
    return super.toString() + ", Hairdresser{" +
        "specialization='" + specialization + '\'' +
        ", rating=" + rating +
        ", number of services=" + beautyServices.size() +
        ", isApproved=" + isApproved +
        '}';
  }


}