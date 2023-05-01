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
}
