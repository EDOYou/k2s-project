package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import java.util.List;
import java.util.Optional;

public interface BeautyServiceService {

  void saveService(BeautyService service);

  /**
   * Finds a beauty service by ID.
   *
   * @param id The ID of the beauty service to find.
   * @return An Optional containing the found beauty service, or an empty Optional if not found.
   */
  Optional<BeautyService> findById(Long id);

  /**
   * Finds all beauty services.
   *
   * @return A list of all beauty services.
   */
  List<BeautyService> findAll();

  List<BeautyService> findAllServices();

  List<String> findDistinctServiceNames();

  Optional<BeautyService> findFirstByName(String name);

  List<BeautyService> findAllByIdIn(List<Long> ids);
}