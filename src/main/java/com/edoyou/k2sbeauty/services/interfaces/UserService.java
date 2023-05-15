package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.User;
import java.util.Optional;

/**
 * UserService interface represents the service layer for User related operations.
 */
public interface UserService {

  /**
   * Find a user in the database by email address.
   *
   * @param email Email address of the user to find.
   * @return An Optional containing the User object if found, otherwise empty.
   */
  Optional<User> findUserByEmail(String email);
}
