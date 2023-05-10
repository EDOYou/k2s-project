package com.edoyou.k2sbeauty.entities.payment;

public enum PaymentStatus {
  NONE,
  PENDING,
  PAID,
  REFUNDED,
  CANCELLED;

  // static helper method in the PaymentStatus enum to get the enum constant by name safely
  public static PaymentStatus fromString(String statusName, PaymentStatus defaultValue) {
    if (statusName != null) {
      for (PaymentStatus status : PaymentStatus.values()) {
        if (status.name().equalsIgnoreCase(statusName)) {
          return status;
        }
      }
    }
    return defaultValue;
  }
}