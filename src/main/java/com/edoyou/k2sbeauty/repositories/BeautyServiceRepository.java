package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link BeautyServiceRepository} interface extends {@link JpaRepository} for database operations
 * on {@link BeautyService} entities. It provides methods to retrieve BeautyService data from the
 * database.
 */
@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {

  /**
   * Retrieves a distinct list of service names from {@link BeautyService} entities.
   *
   * @return a list of distinct service names
   */
  @Query("SELECT DISTINCT b.name FROM BeautyService b")
  List<String> findDistinctServiceNames();

  /**
   * Retrieves the first {@link BeautyService} entity that matches the given name.
   *
   * @param name the name of the BeautyService
   * @return an {@link Optional} containing the BeautyService entity if found, otherwise
   * {@link Optional#empty()}
   */
  Optional<BeautyService> findFirstByName(String name);

  /**
   * Retrieves a list of {@link BeautyService} entities along with their approved
   * {@link com.edoyou.k2sbeauty.entities.model.Hairdresser} entities.
   *
   * @return a list of BeautyService entities with approved Hairdressers
   */
  @Query("SELECT b FROM BeautyService b JOIN FETCH b.hairdressers h WHERE h.isApproved = true")
  List<BeautyService> findAllWithApprovedHairdressers();

  /**
   * Retrieves all {@link BeautyService} entities.
   *
   * @return a list of all BeautyService entities
   */
  @NotNull
  List<BeautyService> findAll();
}