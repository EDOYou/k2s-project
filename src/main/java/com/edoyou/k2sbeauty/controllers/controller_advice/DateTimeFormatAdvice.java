package com.edoyou.k2sbeauty.controllers.controller_advice;

import java.time.format.DateTimeFormatter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class DateTimeFormatAdvice {

  @ModelAttribute("dateTimeFormatter")
  public DateTimeFormatter dateTimeFormatter() {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  }
}