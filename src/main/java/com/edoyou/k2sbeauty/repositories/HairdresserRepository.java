package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link HairdresserRepository} interface extends {@link JpaRepository} for persistence operations
 * on {@link Hairdresser} entities. It provides methods to retrieve Hairdresser data from the
 * database.
 */
@Repository
public interface HairdresserRepository extends JpaRepository<Hairdresser, Long> {

  /**
   * Retrieves a list of {@link Hairdresser} entities who provide a specific beauty service, ordered
   * by their last names.
   *
   * @param serviceId the ID of the beauty service
   * @param sort      the sorting parameters
   * @return a list of Hairdresser entities
   */
  @Query("SELECT h FROM Hairdresser h JOIN h.beautyServices s WHERE s.id = :serviceId AND h.isApproved = true ORDER BY h.lastName")
  List<Hairdresser> findAllByService(@Param("serviceId") Long serviceId, Sort sort);

  /**
   * Retrieves a list of {@link Hairdresser} entities with their associated {@code BeautyServices}.
   *
   * @return a list of Hairdresser entities
   */
  @Query("SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices")
  List<Hairdresser> findAllWithBeautyServices();

  /**
   * Retrieves a page of {@link Hairdresser} entities with their associated {@code BeautyServices}.
   *
   * @param pageable the pagination and sorting information
   * @return a page of Hairdresser entities
   */
  @Query(value = "SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices",
      countQuery = "SELECT count(h) FROM Hairdresser h")
  Page<Hairdresser> findAllWithBeautyServices(Pageable pageable);

  /**
   * Retrieves a page of {@link Hairdresser} entities based on their approval status.
   *
   * @param isApproved the approval status of the hairdressers to be retrieved
   * @param pageable   the pagination and sorting information
   * @return a page of Hairdresser entities
   */
  @Query("SELECT h FROM Hairdresser h WHERE h.isApproved = :isApproved")
  Page<Hairdresser> findByIsApproved(@Param("isApproved") boolean isApproved, Pageable pageable);

  /**
   * Retrieves a list of {@link Hairdresser} entities that are approved.
   *
   * @return a list of approved Hairdresser entities
   */
  List<Hairdresser> findByIsApprovedTrue();

  /**
   * Retrieves a list of {@link Hairdresser} entities that are approved, sorted according to the
   * provided {@code Sort} object.
   *
   * @param sort the sorting parameters
   * @return a sorted list of approved Hairdresser entities
   */
  List<Hairdresser> findByIsApprovedTrue(Sort sort);

  /**
   * Retrieves a {@link Hairdresser} entity and its associated {@code Appointments} by the
   * hairdresser's ID.
   *
   * @param id the ID of the hairdresser to be retrieved
   * @return an {@link Optional} containing the Hairdresser entity if found, otherwise
   * {@link Optional#empty()}
   */
  @Query("SELECT h FROM Hairdresser h JOIN FETCH h.appointments WHERE h.id = :id")
  Optional<Hairdresser> findByIdWithAppointments(@Param("id") Long id);
}