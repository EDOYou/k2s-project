package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <strong>UserServiceImpl</strong> is an implementation of the {@link com.edoyou.k2sbeauty.services.interfaces.UserService} interface.
 * It provides methods for managing, such as creating, updating,
 * deleting, and finding users by their email address or ID. This class
 * uses the {@link com.edoyou.k2sbeauty.repositories.UserRepository} for interacting with the underlying database.
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  //TODO: Save a new user to the database
  public User saveUser(User user) {
    Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
    if (existingUser.isPresent()) {
      throw new IllegalArgumentException("Email already exists.");
    }
    return userRepository.save(user);
  }

  @Override
  //TODO: Update an existing user in the database
  public User updateUser(User user) {
    Optional<User> existingUser = userRepository.findById(user.getId());
    if (existingUser.isEmpty()) {
      throw new IllegalArgumentException("User not found.");
    }
    return userRepository.save(user);
  }

  @Override
  //TODO: Delete a user from the database by their ID
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  //TODO: Find a user by their email address
  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  //TODO: Retrieve all users from the database
  public List<User> findAllUsers() {
    return userRepository.findAll();
  }
}
