package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.repositories.BeautyServiceRepository;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The {@code BeautyServiceServiceImpl} class implements the {@link BeautyServiceService} interface
 * and provides the service layer for beauty service-related operations. This implementation
 * includes logic for creating, updating, deleting, and retrieving beauty services based on various
 * criteria.
 *
 * @see BeautyService
 */
@Service
public class BeautyServiceServiceImpl implements BeautyServiceService {

  private final BeautyServiceRepository beautyServiceRepository;

  @Autowired
  public BeautyServiceServiceImpl(BeautyServiceRepository beautyServiceRepository) {
    this.beautyServiceRepository = beautyServiceRepository;
  }

  /**
   * Creates a new beauty service.
   *
   * @param beautyService The beauty service to create.
   * @return The created beauty service.
   * @throws IllegalArgumentException If the provided beauty service is invalid.
   */
  @Override
  public BeautyService createBeautyService(BeautyService beautyService) {

    if (beautyService.getName() == null || beautyService.getName().isBlank()) {
      throw new IllegalArgumentException("Name have to be filled.");
    }

    if (beautyService.getPrice() <= 0) {
      throw new IllegalArgumentException("Price must be greater than 0.");
    }

    if (beautyService.getDescription() == null || beautyService.getDescription().isBlank()) {
      throw new IllegalArgumentException("You need to add some description of the style.");
    }

    return beautyServiceRepository.save(beautyService);
  }

  /**
   * Updates an existing beauty service.
   *
   * @param id            The ID of the beauty service to update.
   * @param beautyService The updated beauty service.
   * @return The updated beauty service.
   * @throws IllegalArgumentException If the provided ID or beauty service is invalid.
   */
  @Override
  public BeautyService updateBeautyService(Long id, BeautyService beautyService) {
    BeautyService existingBeautyService = beautyServiceRepository.findById(id).orElseThrow(
        () -> new IllegalStateException("Beauty service with id " + id + " does not exist."));

    if (beautyService.getPrice() <= 0) {
      throw new IllegalArgumentException("Price must be greater than 0.");
    }

    existingBeautyService.setName(beautyService.getName());
    existingBeautyService.setDescription(beautyService.getDescription());
    existingBeautyService.setPrice(beautyService.getPrice());

    return beautyServiceRepository.save(existingBeautyService);
  }

  /**
   * Deletes a beauty service by ID.
   *
   * @param id The ID of the beauty service to delete.
   * @throws IllegalArgumentException If the provided ID is invalid.
   */
  @Override
  public void deleteBeautyService(Long id) {
    if (!beautyServiceRepository.existsById(id)) {
      throw new IllegalStateException("Beauty service with id " + id + " does not exist.");
    }

    beautyServiceRepository.deleteById(id);
  }

  /**
   * Finds a beauty service by ID.
   *
   * @param id The ID of the beauty service to find.
   * @return An Optional containing the found beauty service, or an empty Optional if not found.
   */
  @Override
  public Optional<BeautyService> findById(Long id) {
    return beautyServiceRepository.findById(id);
  }

  /**
   * Finds all beauty services.
   *
   * @return A list of all beauty services.
   */
  @Override
  public List<BeautyService> findAll() {
    return beautyServiceRepository.findAllWithHairdressers();
  }

  /**
   * Finds beauty services by a given price range.
   *
   * @param minPrice The minimum price of the beauty services.
   * @param maxPrice The maximum price of the beauty services.
   * @return A list of beauty services within the given price range.
   */
  @Override
  public List<BeautyService> findByPriceRange(Double minPrice, Double maxPrice) {
    if (minPrice == null || maxPrice == null) {
      throw new IllegalArgumentException("Both minimum and maximum price must be provided.");
    }
    if (minPrice <= 0 || maxPrice <= 0) {
      throw new IllegalArgumentException("Minimum and maximum price must be greater than 0.");
    }

    if (minPrice > maxPrice) {
      throw new IllegalArgumentException("Minimum price cannot be greater than maximum price.");
    }

    return beautyServiceRepository.findByPriceBetween(minPrice, maxPrice);
  }
}


