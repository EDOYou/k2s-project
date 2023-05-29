package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * The Client entity class represents a client in the BeautySalon application.
 * It extends the {@link User} class and adds the specific details related to a client.
 */
@Entity
@Table(name = "clients")
public class Client extends User {

  @OneToMany(mappedBy = "client")
  private Set<Appointment> appointments = new HashSet<>();

  public void setAppointments(Set<Appointment> appointments) {
    this.appointments = appointments;
  }

  public Set<Appointment> getAppointments() {
    return appointments;
  }
}