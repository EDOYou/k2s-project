package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;

import java.util.List;

/**
 * HairdresserService interface represents the service layer for Hairdresser related operations.
 *
 * @see Hairdresser
 */
public interface HairdresserService extends UserService {

  /**
   * Find hairdressers by their specialization.
   *
   * @param specialization The specialization to search for.
   * @return A list of hairdressers with the given specialization.
   */
  List<Hairdresser> findBySpecialization(String specialization);

  /**
   * Retrieve all hairdressers from the database with optional sorting.
   *
   * @param sortBy The sorting criteria, can be null.
   * @return A list of Hairdresser objects.
   */
  List<Hairdresser> findAllHairdressers(String sortBy);

  /**
   *
   * @param hairdresserDetails The hairdresser with updated information.
   * @return The updated role.
   * @throws IllegalArgumentException If the given hairdresser's ID is null.
   * @throws com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException If the hairdresser is not found in the repository.
   */
  Hairdresser updateHairdresser(Hairdresser hairdresserDetails);

  /**
   * Deleted the hairdresser from the repository.
   *
   * @param id ID of the target hairdresser.
   * @throws com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException If the hairdresser is not found in the repository.
   */
  void deleteHairdresser(Long id);
}