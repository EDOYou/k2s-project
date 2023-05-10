package com.edoyou.k2sbeauty.dto;

import java.time.LocalDateTime;

public class AppointmentWithoutPaymentDTO {
  private Long id;
  private LocalDateTime appointmentTime;
  private String customerName;

  public AppointmentWithoutPaymentDTO(Long id, LocalDateTime appointmentTime, String customerName) {
    this.id = id;
    this.appointmentTime = appointmentTime;
    this.customerName = customerName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getAppointmentTime() {
    return appointmentTime;
  }

  public void setAppointmentTime(LocalDateTime appointmentTime) {
    this.appointmentTime = appointmentTime;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }
}
