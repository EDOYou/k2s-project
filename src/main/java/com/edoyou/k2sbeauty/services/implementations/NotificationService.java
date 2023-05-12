package com.edoyou.k2sbeauty.services.implementations;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final JavaMailSender javaMailSender;

  @Autowired
  public NotificationService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  public void sendNotification(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    javaMailSender.send(message);
  }
}
