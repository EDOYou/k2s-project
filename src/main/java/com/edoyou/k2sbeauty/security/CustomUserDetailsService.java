package com.edoyou.k2sbeauty.security;

import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private static final Logger logger = Logger.getLogger(CustomUserDetailsService.class.getName());

  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    logger.info("Loading user with email: {" + email + "}");
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User is not found."));
    logger.info("Stored password for user with email {"+email+"}: {"+user.getPassword()+"}");

    if (user == null) {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }

    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
        new ArrayList<>());
  }
}