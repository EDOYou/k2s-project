package com.edoyou.k2sbeauty.entities.model;

import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus paymentStatus;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isCompleted;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(nullable = false)
  private Client client;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(nullable = false)
  private Hairdresser hairdresser;

  @Column(nullable = false)
  private LocalDateTime appointmentTime;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(nullable = false)
  private BeautyService beautyService;

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

  public void setBeautyService(BeautyService beautyService) {
    this.beautyService = beautyService;
  }

  public void setPaymentStatus(PaymentStatus paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public void setCompleted(boolean completed) {
    isCompleted = completed;
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

  public BeautyService getBeautyService() {
    return beautyService;
  }

  public PaymentStatus getPaymentStatus() {
    return paymentStatus;
  }

  public boolean isCompleted() {
    return isCompleted;
  }
}
