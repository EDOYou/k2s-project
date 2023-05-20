package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
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

  @Query("SELECT DISTINCT b.name FROM BeautyService b")
  List<String> findDistinctServiceNames();

  Optional<BeautyService> findFirstByName(String name);

  @Query("SELECT b FROM BeautyService b JOIN FETCH b.hairdressers h WHERE h.isApproved = true")
  List<BeautyService> findAllWithApprovedHairdressers();

  @NotNull
  List<BeautyService> findAll();
}