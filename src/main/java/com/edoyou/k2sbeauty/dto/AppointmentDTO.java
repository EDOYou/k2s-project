package com.edoyou.k2sbeauty.dto;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import java.util.List;

public class AppointmentDTO {

  private List<Appointment> completedAppointments;
  private List<Appointment> pendingAppointments;

  public AppointmentDTO(List<Appointment> completedAppointments,
      List<Appointment> pendingAppointments) {
    this.completedAppointments = completedAppointments;
    this.pendingAppointments = pendingAppointments;
  }

  public List<Appointment> getCompletedAppointments() {
    return completedAppointments;
  }

  public void setCompletedAppointments(List<Appointment> completedAppointments) {
    this.completedAppointments = completedAppointments;
  }

  public List<Appointment> getPendingAppointments() {
    return pendingAppointments;
  }

  public void setPendingAppointments(List<Appointment> pendingAppointments) {
    this.pendingAppointments = pendingAppointments;
  }
}