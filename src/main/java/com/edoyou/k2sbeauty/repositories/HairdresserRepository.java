package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HairdresserRepository is a JPA repository for handling data access and
 * manipulation of the Hairdresser entity.
 *
 * @see JpaRepository
 * @see Hairdresser
 */
@Repository
public interface HairdresserRepository extends JpaRepository<Hairdresser, Long> {

  /**
   * Finds hairdressers with a given specialization.
   *
   * @param specialization the specialization to search for.
   * @return a list of hairdressers with the specified specialization.
   */
  List<Hairdresser> findBySpecialization(String specialization);

  /**
   * Finds all hairdressers providing a specific beauty service, sorted according to the given Sort parameter.
   *
   * @param serviceId the ID of the beauty service to filter hairdressers by.
   * @param sort      the sort criteria to apply to the result set.
   * @return a sorted list of hairdressers providing the specified beauty service.
   */
  @Query("SELECT h FROM Hairdresser h JOIN h.beautyServices s WHERE s.id = :serviceId")
  List<Hairdresser> findAllByService(@Param("serviceId") Long serviceId, Sort sort);

  @Query("SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices")
  List<Hairdresser> findAllWithBeautyServices();

  List<Hairdresser> findByIsApproved(boolean isApproved);
}