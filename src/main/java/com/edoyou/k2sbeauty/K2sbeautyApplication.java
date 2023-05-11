package com.edoyou.k2sbeauty;

import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class K2sbeautyApplication {

  public static void main(String[] args) {
    // Setting timezone according to the database which is UTC
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    System.out.println(ZoneId.systemDefault());
    SpringApplication.run(K2sbeautyApplication.class, args);
//		PasswordUpdater passwordUpdater = context.getBean(PasswordUpdater.class);
//		passwordUpdater.updatePasswords();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}