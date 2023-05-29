package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;

import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

/**
 * {@code HairdresserService} is an interface providing specifications for business operations
 * applicable to {@link Hairdresser} entities.
 */
public interface HairdresserService extends UserService {

    /**
     * Persists a {@link Hairdresser} entity to the repository.
     *
     * @param hairdresser The {@link Hairdresser} entity to be saved.
     */
    void saveHairdresser(Hairdresser hairdresser);

    /**
     * Retrieves a {@link List} of all {@link Hairdresser} entities sorted by a specified field.
     *
     * @param sortBy The field by which to sort the retrieved entities.
     * @return A {@link List} of all {@link Hairdresser} entities, sorted as specified.
     */
    List<Hairdresser> findAllHairdressers(String sortBy);

    /**
     * Retrieves a list of all hairdresser entities.
     *
     * @return a list of all {@link Hairdresser} entities
     */
    List<Hairdresser> findAllHairdressers();

    /**
     * Retrieves a list of all {@link Hairdresser} entities that provide a certain beauty service,
     * sorted by a specific field.
     *
     * @param sortBy    the field by which to sort the retrieved entities
     * @param serviceId the ID of the beauty service
     * @return a sorted list of all {@link Hairdresser} entities that provide a certain beauty service
     */
    List<Hairdresser> findAllHairdressersByServiceId(String sortBy, Long serviceId);

    /**
     * Retrieves a {@link Hairdresser} entity by its ID.
     *
     * @param id the ID of the hairdresser
     * @return a {@link Hairdresser} entity
     */
    Hairdresser findById(Long id);

    /**
     * Retrieves a list of all {@link Hairdresser} entities along with their associated beauty
     * services.
     *
     * @return a list of all {@link Hairdresser} entities along with their associated beauty services
     */
    @Query("SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices")
    List<Hairdresser> findAllWithBeautyServices();

    /**
     * Retrieves a page of {@link Hairdresser} entities along with their associated beauty services.
     *
     * @param pageable pagination information
     * @return a page of {@link Hairdresser} entities along with their associated beauty services
     */
    @Query("SELECT h FROM Hairdresser h JOIN FETCH h.beautyServices")
    Page<Hairdresser> findAllWithBeautyServices(Pageable pageable);

    /**
     * Deletes a {@link Hairdresser} entity by its ID.
     *
     * @param id the ID of the hairdresser to delete
     */
    void deleteHairdresser(Long id);

    /**
     * Retrieves a page of {@link Hairdresser} entities by their approval status.
     *
     * @param isApproved the approval status to filter by
     * @param pageable   pagination information
     * @return a page of {@link Hairdresser} entities with the provided approval status
     */
    Page<Hairdresser> findAllHairdressersByApprovalStatus(boolean isApproved, Pageable pageable);

    /**
     * Generates a schedule for a {@link Hairdresser} entity.
     *
     * @param hairdresser the hairdresser entity for which to generate a schedule
     * @return a map of dates to time slots representing the schedule
     */
    Map<LocalDate, List<TimeSlot>> generateSchedule(Hairdresser hairdresser);

    /**
     * Updates the rating of a {@link Hairdresser} entity.
     *
     * @param hairdresser the hairdresser entity for which to update the rating
     */
    void updateRating(Hairdresser hairdresser);
}