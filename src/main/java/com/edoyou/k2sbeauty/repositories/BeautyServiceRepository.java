package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The {@code BeautyServiceRepository} interface extends {@link JpaRepository} and provides methods
 * for querying the {@link BeautyService} entity. This repository enables the retrieval and
 * manipulation of beauty service-related data from the database.
 *
 * @see BeautyService
 */
@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {

//  /**
//   * Finds beauty services whose name contains the specified string (case-insensitive).
//   *
//   * @param name the name to search for in beauty service names
//   * @return a list of {@link BeautyService} instances whose names contain the specified string
//   */
//  List<BeautyService> findByNameContainingIgnoreCase(String name);


  /**
   * Finds beauty services within the specified price range.
   *
   * @param minPrice the minimum price for the price range
   * @param maxPrice the maximum price for the price range
   * @return a list of {@link BeautyService} instances within the specified price range
   */
  List<BeautyService> findByPriceBetween(Double minPrice, Double maxPrice);

  /**
   * Fetches all beauty services along with their associated hairdressers using a single query. This
   * method is useful for minimizing the number of database queries when both beauty services and
   * their hairdressers are needed.
   *
   * @return a list of {@link BeautyService} instances with their associated hairdressers
   */
  @Query("SELECT DISTINCT s FROM BeautyService s JOIN FETCH s.hairdressers where s.name IS NOT NULL")
  List<BeautyService> findAllWithHairdressers();

  @Query("SELECT DISTINCT b.name FROM BeautyService b")
  List<String> findDistinctServiceNames();

  Optional<BeautyService> findByName(String name);

  Optional<BeautyService> findFirstByName(String name);

  @Query("SELECT b FROM BeautyService b JOIN FETCH b.hairdressers h WHERE h.isApproved = true")
  List<BeautyService> findAllWithApprovedHairdressers();

  List<BeautyService> findAllByIdIn(List<Long> ids);
}