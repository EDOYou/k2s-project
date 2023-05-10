package com.edoyou.k2sbeauty.exceptions;

public class AppointmentNotFoundException extends RuntimeException {

  public AppointmentNotFoundException(String message) {
    super(message);
  }
}