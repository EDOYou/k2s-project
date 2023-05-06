package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hairdressers")
public class Hairdresser extends User {

  @Column(nullable = false)
  private String specialization;

  @OneToMany(mappedBy = "hairdresser")
  private Set<BeautyService> beautyServices = new HashSet<>();

  @OneToMany(mappedBy = "hairdresser")
  private Set<Appointment> appointments = new HashSet<>();

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public void setAppointments(Set<Appointment> appointments) {
    this.appointments = appointments;
  }

  public void setBeautyServices(Set<BeautyService> beautyServices) {
    this.beautyServices = beautyServices;
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
}
