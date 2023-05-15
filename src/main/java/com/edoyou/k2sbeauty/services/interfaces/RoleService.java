package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Role;
import java.util.Optional;

public interface RoleService {

  /**
   * Retrieves a role by its name.
   *
   * @param name The name of the role.
   * @return An Optional containing the role if found, or empty if not found.
   */
  Optional<Role> getRoleByName(String name);
}