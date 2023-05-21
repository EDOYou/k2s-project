package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.repositories.BeautyServiceRepository;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  private static final Logger LOGGER = LogManager.getLogger(
      BeautyServiceServiceImpl.class.getName());

  private final BeautyServiceRepository beautyServiceRepository;

  @Autowired
  public BeautyServiceServiceImpl(BeautyServiceRepository beautyServiceRepository) {
    this.beautyServiceRepository = beautyServiceRepository;
  }

  @Override
  public void saveService(BeautyService service) {
    LOGGER.info("Saving a beauty service...");
    if (service == null) {
      throw new NullPointerException("Service cannot be NULL");
    }
    beautyServiceRepository.save(service);
  }

  /**
   * Finds a beauty service by ID.
   *
   * @param id The ID of the beauty service to find.
   * @return An Optional containing the found beauty service, or an empty Optional if not found.
   */
  @Override
  public Optional<BeautyService> findById(Long id) {
    if (id == null) {
      LOGGER.error("Attempted to find service with null ID");
      throw new IllegalArgumentException("ID cannot be null");
    }
    LOGGER.info("Find service by its ID...");
    return beautyServiceRepository.findById(id);
  }

  /**
   * Finds all beauty services.
   *
   * @return A list of all beauty services.
   */
  @Override
  public List<BeautyService> findAll() {
    LOGGER.info("Find all services with approved hairdressers...");
    return beautyServiceRepository.findAllWithApprovedHairdressers();
  }

  @Override
  public Page<BeautyService> findAll(Pageable pageable) {
    return beautyServiceRepository.findAll(pageable);
  }

  @Override
  public List<BeautyService> findAllServices() {
    LOGGER.info("Find all services...");
    return beautyServiceRepository.findAll();
  }

  @Override
  public List<String> findDistinctServiceNames() {
    LOGGER.info("Find distinct service names...");
    return beautyServiceRepository.findDistinctServiceNames();
  }

  @Override
  public Optional<BeautyService> findFirstByName(String name) {
    LOGGER.info("Find first service by name...");
    return beautyServiceRepository.findFirstByName(name);
  }

  @Override
  public List<BeautyService> findAllByIdIn(List<Long> ids) {
    if (ids == null) {
      LOGGER.info("No IDs provided. Returning an empty list.");
      return new ArrayList<>();
    }

    try {
      LOGGER.info("Fetching beauty services by their IDs.");
      return beautyServiceRepository.findAllById(ids);
    } catch (Exception e) {
      LOGGER.error("An error occurred while fetching beauty services by their IDs." + e);
      throw new RuntimeException(e);
    }
  }
}