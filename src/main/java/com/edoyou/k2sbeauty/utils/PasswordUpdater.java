//package com.edoyou.k2sbeauty.utils;
//
//import com.edoyou.k2sbeauty.entities.model.Client;
//import com.edoyou.k2sbeauty.entities.model.User;
//import com.edoyou.k2sbeauty.repositories.ClientRepository;
//import com.edoyou.k2sbeauty.repositories.UserRepository;
//import jakarta.annotation.PostConstruct;
//import java.util.logging.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class PasswordUpdater {
//
//  private final PasswordEncoder passwordEncoder;
//  private final ClientRepository clientRepository;
//
//  @Autowired
//  public PasswordUpdater(PasswordEncoder passwordEncoder, ClientRepository clientRepository, UserRepository userRepository) {
//    this.passwordEncoder = passwordEncoder;
//    this.clientRepository = clientRepository;
//  }
//
//  @EventListener
//  public void onApplicationReady(ContextRefreshedEvent event) {
//    updatePasswords();
//  }
//
//  public void updatePasswords() {
//    List<Client> clients = clientRepository.findAll();
//
//    clients.forEach(client -> {
//      String hashedPassword = passwordEncoder.encode(client.getPassword());
//      client.setPassword(hashedPassword);
//    });
//
//    clientRepository.saveAll(clients);
//  }
//}