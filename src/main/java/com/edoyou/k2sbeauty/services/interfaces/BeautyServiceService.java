package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import java.util.List;
import java.util.Optional;

public interface BeautyServiceService {

  void saveService(BeautyService service);

  /**
   * Creates a new beauty service.
   *
   * @param beautyService The beauty service to create.
   * @return The created beauty service.
   * @throws IllegalArgumentException If the provided beauty service is invalid.
   */
  BeautyService createBeautyService(BeautyService beautyService);

  /**
   * Updates an existing beauty service.
   *
   * @param id            The ID of the beauty service to update.
   * @param beautyService The updated beauty service.
   * @return The updated beauty service.
   * @throws IllegalArgumentException If the provided ID or beauty service is invalid.
   */
  BeautyService updateBeautyService(Long id, BeautyService beautyService);

  /**
   * Deletes a beauty service by ID.
   *
   * @param id The ID of the beauty service to delete.
   * @throws IllegalArgumentException If the provided ID is invalid.
   */
  void deleteBeautyService(Long id);

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

  /**
   * Finds beauty services by a given price range.
   *
   * @param minPrice The minimum price of the beauty services.
   * @param maxPrice The maximum price of the beauty services.
   * @return A list of beauty services within the given price range.
   */
  List<BeautyService> findByPriceRange(Double minPrice, Double maxPrice);

  List<String> findDistinctServiceNames();

  Optional<BeautyService> findByName(String serviceName);

  Optional<BeautyService> findFirstByName(String name);

  List<BeautyService> findAllByIdIn(List<Long> ids);
}