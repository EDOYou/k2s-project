package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.User;
import java.util.List;
import java.util.Optional;

/**
 * UserService interface represents the service layer for User related operations.
 */

public interface UserService {

  /**
   * Save a new user to the database.
   *
   * @param user User to be saved.
   * @return The saved User object.
   */
  User saveUser(User user);

  /**
   * Update an existing user in the database.
   *
   * @param user User to be updated.
   * @return The updated User object.
   */
  User updateUser(User user);

  void updateRole(User user, String roleName);

  /**
   * Delete a user from the database by ID.
   *
   * @param id ID of the user to be deleted.
   */
  void deleteUser(Long id);

  /**
   * Find a user in the database by email address.
   *
   * @param email Email address of the user to find.
   * @return An Optional containing the User object if found, otherwise empty.
   */
  Optional<User> findUserByEmail(String email);

  /**
   * Find a user in the database by ID.
   *
   * @param id ID of the user to find.
   * @return An Optional containing the User object if found, otherwise empty.
   */
  Optional<User> findUserById(Long id);

  /**
   * Retrieve all users from the database.
   *
   * @return A list of User objects.
   */
  List<User> findAllUsers();
}
