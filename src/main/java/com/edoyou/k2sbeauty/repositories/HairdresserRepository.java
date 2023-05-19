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
 * HairdresserRepository is a JPA repository for handling data access and manipulation of the
 * Hairdresser entity.
 *
 * @see JpaRepository
 * @see Hairdresser
 */
@Repository
public interface HairdresserRepository extends JpaRepository<Hairdresser, Long> {

  /**
   * Finds all hairdressers providing a specific beauty service, sorted according to the given Sort
   * parameter.
   *
   * @param serviceId the ID of the beauty service to filter hairdressers by.
   * @param sort      the sort criteria to apply to the result set.
   * @return a sorted list of hairdressers providing the specified beauty service.
   */
  @Query("SELECT h FROM Hairdresser h JOIN h.beautyServices s WHERE s.id = :serviceId AND h.isApproved = true ORDER BY h.lastName")
  List<Hairdresser> findAllByService(@Param("serviceId") Long serviceId, Sort sort);

  @Query("SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices")
  List<Hairdresser> findAllWithBeautyServices();

  @Query(value = "SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices",
      countQuery = "SELECT count(h) FROM Hairdresser h")
  Page<Hairdresser> findAllWithBeautyServices(Pageable pageable);

  @Query("SELECT h FROM Hairdresser h WHERE h.isApproved = :isApproved")
  Page<Hairdresser> findByIsApproved(@Param("isApproved") boolean isApproved, Pageable pageable);


  List<Hairdresser> findByIsApprovedTrue();

  List<Hairdresser> findByIsApprovedTrue(Sort sort);

  @Query("SELECT h FROM Hairdresser h JOIN FETCH h.appointments WHERE h.id = :id")
  Optional<Hairdresser> findByIdWithAppointments(@Param("id") Long id);
}