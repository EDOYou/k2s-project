package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import java.util.List;
import java.util.Optional;

public interface RoleService {

  /**
   * Assigns a role to a user.
   *
   * @param userId The ID of the user.
   * @param roleId The ID of the role to assign.
   * @return The updated user with the assigned role.
   * @throws IllegalStateException If the user or role does not exist.
   */
  User assignRoleToUser(Long userId, Long roleId);

  /**
   * Creates a new role.
   *
   * @param role The role to create.
   * @return The created role.
   * @throws IllegalStateException If the role name already exists.
   */
  Role createRole(Role role);

  /**
   * Retrieves a role by its ID.
   *
   * @param id The ID of the role.
   * @return An Optional containing the role if found, or empty if not found.
   */
  Optional<Role> getRoleById(Long id);

  /**
   * Retrieves a role by its name.
   *
   * @param name The name of the role.
   * @return An Optional containing the role if found, or empty if not found.
   */
  Optional<Role> getRoleByName(String name);

  /**
   * Retrieves all roles.
   *
   * @return A list of all roles.
   */
  List<Role> getAllRoles();

  /**
   * Updates an existing role.
   *
   * @param role The role with updated information.
   * @return The updated role.
   * @throws IllegalStateException If the role does not exist.
   */
  Role updateRole(Role role);

  /**
   * Deletes a role by its ID.
   *
   * @param id The ID of the role to delete.
   * @throws IllegalStateException If the role does not exist.
   */
  void deleteRoleById(Long id);
}