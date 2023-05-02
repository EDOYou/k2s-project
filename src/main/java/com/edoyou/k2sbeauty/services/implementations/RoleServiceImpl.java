package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * A service implementation for managing roles in the application.
 * Provides methods for creating, retrieving, updating, and deleting roles,
 * as well as assigning roles to users.
 *
 * @see Role
 * @see RoleRepository
 * @see UserRepository
 */
@Service
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  @Autowired
  public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }

  /**
   * Assigns a role to a user.
   *
   * @param userId The ID of the user.
   * @param roleId The ID of the role to assign.
   * @return The updated user with the assigned role.
   * @throws IllegalStateException If the user or role does not exist.
   */
  @Override
  public User assignRoleToUser(Long userId, Long roleId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    Optional<Role> optionalRole = roleRepository.findById(roleId);

    if (optionalUser.isEmpty()) {
      throw new IllegalStateException("User with id " + userId + " does not exist.");
    }

    if (optionalRole.isEmpty()) {
      throw new IllegalStateException("Role with id " + roleId + " does not exist.");
    }

    User user = optionalUser.get();
    Role role = optionalRole.get();
    Set<Role> userRoles = user.getRoles();
    userRoles.add(role);
    user.setRoles(userRoles);

    return userRepository.save(user);
  }

  @Override
  public Role createRole(Role role) {
    if (roleRepository.findByName(role.getName()).isPresent()) {
      throw new IllegalStateException("Role with name " + role.getName() + " already exists.");
    }
    return roleRepository.save(role);
  }

  @Override
  public Optional<Role> getRoleById(Long id) {
    return roleRepository.findById(id);
  }

  @Override
  public Optional<Role> getRoleByName(String name) {
    return roleRepository.findByName(name);
  }

  @Override
  public List<Role> getAllRoles() {
    return roleRepository.findAll();
  }

  @Override
  public Role updateRole(Role role) {
    if (!roleRepository.existsById(role.getId())) {
      throw new IllegalStateException("Role with id " + role.getId() + " does not exist.");
    }
    return roleRepository.save(role);
  }

  @Override
  public void deleteRoleById(Long id) {
    if (!roleRepository.existsById(id)) {
      throw new IllegalStateException("Role with id " + id + " does not exist.");
    }
    roleRepository.deleteById(id);
  }
}