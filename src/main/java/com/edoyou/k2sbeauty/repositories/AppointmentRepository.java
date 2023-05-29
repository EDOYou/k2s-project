package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link AppointmentRepository} interface extends {@link JpaRepository} for performing database
 * operations on {@link Appointment} entities. It provides methods to retrieve Appointment data from
 * the database.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  /**
   * Retrieves all {@link Appointment} entities in a paginated format.
   *
   * @param pageable {@link Pageable} instance containing pagination information
   * @return a {@link Page} of Appointment entities
   */
  @NotNull
  Page<Appointment> findAll(@NotNull Pageable pageable);

  /**
   * Retrieves a list of {@link Appointment} entities by {@link Client}.
   *
   * @param client the Client whose Appointments are to be retrieved
   * @return a list of Appointment entities
   */
  List<Appointment> findByClient(Client client);

  /**
   * Retrieves a list of {@link Appointment} entities by {@link Hairdresser}.
   *
   * @param hairdresser the Hairdresser whose Appointments are to be retrieved
   * @return a list of Appointment entities
   */
  List<Appointment> findByHairdresser(Hairdresser hairdresser);
}