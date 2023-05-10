package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.exceptions.RoleNotFoundException;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.UserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User saveUser(User user) {
    Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
    if (existingUser.isPresent()) {
      throw new IllegalArgumentException("Email already exists.");
    }

    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);

    return userRepository.save(user);
  }

  @Override
  public User updateUser(User user) {
    Optional<User> existingUser = userRepository.findById(user.getId());
    if (existingUser.isEmpty()) {
      throw new IllegalArgumentException("User not found.");
    }

    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
      String hashedPassword = passwordEncoder.encode(user.getPassword());
      user.setPassword(hashedPassword);
    }

    return userRepository.save(user);
  }

  @Override
  public void updateRole(User user, String roleName) {
    LOGGER.info("Updating role for user with id: {" + user.getId() + "}");
    try {
      Role role = roleRepository.findByName(roleName)
          .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
      Set<Role> roles = user.getRoles();
      roles.add(role);
      user.setRoles(roles);
      userRepository.save(user);
      LOGGER.info("Role updated successfully for user with id: {" + user.getId() + "}");
    } catch (RoleNotFoundException e) {
      LOGGER.severe(
          "Error updating role for user with id: {" + user.getId() + "}: {" + e.getMessage() + "}");
      throw e;
    } catch (Exception e) {
      LOGGER.severe("Error updating role for user with id: {" + user.getId() + "}" + e);
      throw new RuntimeException("An unexpected error occurred while updating the role.");
    }
  }

  @Override
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public List<User> findAllUsers() {
    return userRepository.findAll();
  }
}