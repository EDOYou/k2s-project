package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.UserService;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <strong>UserServiceImpl</strong> is an implementation of the
 * {@link com.edoyou.k2sbeauty.services.interfaces.UserService} interface. It provides methods for
 * managing, such as creating, updating, deleting, and finding users by their email address or ID.
 * This class uses the {@link com.edoyou.k2sbeauty.repositories.UserRepository} for interacting with
 * the underlying database.
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

  private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class.getName());
  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    LOGGER.info("Finding user by his/her name ...");
    return userRepository.findByEmail(email);
  }

}