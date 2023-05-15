package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;

import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.Query;

/**
 * HairdresserService interface represents the service layer for Hairdresser related operations.
 *
 * @see Hairdresser
 */
public interface HairdresserService extends UserService {

  void saveHairdresser(Hairdresser hairdresser);

  /**
   * Retrieve all hairdressers from the database with optional sorting.
   *
   * @param sortBy The sorting criteria, can be null.
   * @return A list of Hairdresser objects.
   */
  List<Hairdresser> findAllHairdressers(String sortBy);

  List<Hairdresser> findAllHairdressers();

  /**
   * Finds all hairdressers based on the given sorting criteria and service ID.
   *
   * @param sortBy    The field to sort hairdressers by.
   * @param serviceId The ID of the beauty service to filter hairdressers by.
   * @return A list of hairdressers sorted by the given criteria and filtered by the given service
   * ID.
   */
  List<Hairdresser> findAllHairdressersByServiceId(String sortBy, Long serviceId);

  Hairdresser findById(Long id);

  @Query("SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices")
  List<Hairdresser> findAllWithBeautyServices();

  /**
   * Deleted the hairdresser from the repository.
   *
   * @param id ID of the target hairdresser.
   * @throws com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException If the hairdresser is not
   *                                                                   found in the repository.
   */
  void deleteHairdresser(Long id);

  List<Hairdresser> findAllHairdressersByApprovalStatus(boolean isApproved);

  Map<LocalDate, List<TimeSlot>> generateSchedule(Hairdresser hairdresser);

  void updateRating(Hairdresser hairdresser);
}