package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.repositories.WorkingHoursRepository;
import com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@code WorkingHoursServiceImpl} is an implementation of the {@code WorkingHoursService} interface.
 * It provides operations to manage {@link WorkingHours} objects, which represent the working hours
 * of a hairdresser in the system.
 * This class uses a {@link WorkingHoursRepository} object to interact with the underlying database.
 * It is annotated with @Service to indicate that it's a service that can be injected where needed.
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Service
public class WorkingHoursServiceImpl implements WorkingHoursService {

    private static final Logger LOGGER = LogManager.getLogger(
            WorkingHoursServiceImpl.class.getName());
    private final WorkingHoursRepository workingHoursRepository;

    /**
     * Constructs a new {@code WorkingHoursServiceImpl} with the specified {@link WorkingHoursRepository}.
     *
     * @param workingHoursRepository The repository which will be used to access working hours data.
     */
    @Autowired
    public WorkingHoursServiceImpl(WorkingHoursRepository workingHoursRepository) {
        this.workingHoursRepository = workingHoursRepository;
    }

    /**
     * Saves the provided {@link WorkingHours} object to the database.
     *
     * @param workingHours The working hours object to be saved.
     */
    @Override
    public void saveWorkingHours(WorkingHours workingHours) {
        LOGGER.info("Saving working hours...");
        workingHoursRepository.save(workingHours);
    }

    /**
     * Deletes the {@link WorkingHours} entity with the specified ID from the database.
     *
     * @param id The ID of the working hours entity to be deleted.
     */
    @Override
    public void deleteWorkingHours(Long id) {
        LOGGER.info("Deleting working hours...");
        workingHoursRepository.deleteById(id);
    }

    /**
     * Finds a {@link WorkingHours} entity based on the provided day of the week, start time, and end time.
     *
     * @param dayOfWeek The day of the week.
     * @param start     The start time.
     * @param end       The end time.
     * @return An Optional containing the found {@link WorkingHours} entity, or an empty Optional if no entity was found.
     */
    public Optional<WorkingHours> findByDayOfWeekAndStartAndEnd(DayOfWeek dayOfWeek, LocalTime start,
                                                                LocalTime end) {
        LOGGER.info("Find By Day Of Week And Start And End..");
        return workingHoursRepository.findByDayOfWeekAndStartAndEnd(dayOfWeek, start, end);
    }
}