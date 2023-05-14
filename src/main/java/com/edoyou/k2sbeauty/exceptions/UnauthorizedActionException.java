package com.edoyou.k2sbeauty.exceptions;

public class UnauthorizedActionException extends RuntimeException {

  public UnauthorizedActionException(String message) {
    super(message);
  }
}