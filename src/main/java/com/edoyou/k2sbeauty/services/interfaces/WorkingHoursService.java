package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.WorkingHours;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

/**
 * {@code WorkingHoursService} is an interface providing specifications for business operations
 * applicable to {@link WorkingHours} entities.
 */
public interface WorkingHoursService {

    /**
     * Persists a new {@link WorkingHours} instance into the repository.
     *
     * @param workingHours The {@link WorkingHours} instance to be saved.
     */
    void saveWorkingHours(WorkingHours workingHours);

    /**
     * Deletes a {@link WorkingHours} instance from the repository based on its identifier.
     *
     * @param id The identifier of the {@link WorkingHours} instance to be deleted.
     */
    void deleteWorkingHours(Long id);

    /**
     * Retrieves a {@link WorkingHours} instance based on its day of the week, start time, and end time.
     *
     * @param value The day of the week.
     * @param start The start time.
     * @param end   The end time.
     * @return An Optional that may contain the found {@link WorkingHours} instance.
     */
    Optional<WorkingHours> findByDayOfWeekAndStartAndEnd(DayOfWeek value, LocalTime start,
                                                         LocalTime end);
}