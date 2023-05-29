package com.edoyou.k2sbeauty.entities.model.appointment_details;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import java.time.LocalDateTime;

/**
 * The {@link TimeSlot} class represents a slot of time during which an {@link Appointment} can be scheduled.
 * <p>
 * Each TimeSlot has a start and end time, and it can be associated with an appointment.
 * </p>
 */
public class TimeSlot {

  private LocalDateTime start;
  private LocalDateTime end;
  private Appointment appointment;

  public TimeSlot(LocalDateTime start, LocalDateTime end, Appointment appointment) {
    this.start = start;
    this.end = end;
    this.appointment = appointment;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public void setStart(LocalDateTime start) {
    this.start = start;
  }

  public LocalDateTime getEnd() {
    return end;
  }

  public void setEnd(LocalDateTime end) {
    this.end = end;
  }

  public Appointment getAppointment() {
    return appointment;
  }

  public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
  }
}