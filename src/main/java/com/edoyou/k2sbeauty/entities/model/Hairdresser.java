package com.edoyou.k2sbeauty.entities.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hairdressers")
public class Hairdresser extends User {
  @Column(nullable = false)
  private String specialization;

  @OneToMany(mappedBy = "hairdresser")
  private Set<Appointment> appointments = new HashSet<>();

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public void setAppointments(Set<Appointment> appointments) {
    this.appointments = appointments;
  }

  public String getSpecialization() {
    return specialization;
  }

  public Set<Appointment> getAppointments() {
    return appointments;
  }
}
