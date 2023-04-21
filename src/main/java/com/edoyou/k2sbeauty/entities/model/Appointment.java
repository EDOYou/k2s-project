package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Client client;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Hairdresser hairdresser;

  @Column(nullable = false)
  private LocalDateTime appointmentTime;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Service service;

  public void setId(Long id) {
    this.id = id;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public void setHairdresser(Hairdresser hairdresser) {
    this.hairdresser = hairdresser;
  }

  public void setAppointmentTime(LocalDateTime appointmentTime) {
    this.appointmentTime = appointmentTime;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public Long getId() {
    return id;
  }

  public Client getClient() {
    return client;
  }

  public Hairdresser getHairdresser() {
    return hairdresser;
  }

  public LocalDateTime getAppointmentTime() {
    return appointmentTime;
  }

  public Service getService() {
    return service;
  }
}
