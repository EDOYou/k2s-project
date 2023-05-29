package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The {@code WorkingHoursRepository} interface extends {@link JpaRepository} and provides methods
 * for querying the {@link WorkingHours} entity. It allows operations for fetching specific working
 * hours based on day of week, start and end times.
 *
 * @see WorkingHours
 */
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

  /**
   * Fetches the {@link WorkingHours} entity based on provided day of week, start and end times.
   *
   * @param dayOfWeek The {@link DayOfWeek} for which the working hours are to be fetched.
   * @param start     The start {@link LocalTime} of the working hours.
   * @param end       The end {@link LocalTime} of the working hours.
   * @return an {@link Optional} containing the WorkingHours entity if found, otherwise
   * {@link Optional#empty()}
   */
  Optional<WorkingHours> findByDayOfWeekAndStartAndEnd(DayOfWeek dayOfWeek, LocalTime start,
      LocalTime end);
}
