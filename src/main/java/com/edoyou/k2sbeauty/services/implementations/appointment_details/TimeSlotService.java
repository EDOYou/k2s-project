package com.edoyou.k2sbeauty.services.implementations.appointment_details;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * {@code TimeSlotService} is a service that is responsible for generating and managing time slots
 * for a hairdresser's schedule. It uses the {@link Hairdresser}'s working hours to generate a
 * list of available time slots for services over the coming week. Each time slot is created with
 * a duration equal to the time required for one service (90 minutes by default). It's annotated
 * with @Service to indicate that it's a service that can be injected where needed.
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Service
public class TimeSlotService {

    public static final int ONE_SERVICE_TIME = 90;
    private static final int WEEK_DAYS = 7;
    private static final Logger LOGGER = LogManager.getLogger(TimeSlotService.class.getName());

    /**
     * Generates a list of time slots for the specified hairdresser. Each time slot spans the time
     * required for one service and fits within the hairdresser's working hours. Time slots are
     * generated for the next seven days starting from the current date and time.
     *
     * @param hairdresser The hairdresser for whom to generate time slots.
     * @return The generated list of time slots.
     */
    public List<TimeSlot> generateTimeSlots(Hairdresser hairdresser) {
        List<TimeSlot> timeSlots = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < WEEK_DAYS; i++) {
            LocalDateTime dateTime = now.plusDays(i);
            DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

            addTimeSlotsForDay(hairdresser, timeSlots, dateTime, dayOfWeek);
        }

        return timeSlots;
    }

    /**
     * Adds time slots to the provided list for a specific day of the week, according to the
     * hairdresser's working hours for that day. Each time slot spans the time required for one
     * service. If the hairdresser's working hours for the day cross midnight, the method correctly
     * handles the creation of time slots that span two different days.
     *
     * @param hairdresser The hairdresser for whom to add time slots.
     * @param timeSlots   The list of time slots to which to add.
     * @param dateTime    The date and time representing the specific day of the week.
     * @param dayOfWeek   The specific day of the week.
     */
    private static void addTimeSlotsForDay(Hairdresser hairdresser, List<TimeSlot> timeSlots,
                                           LocalDateTime dateTime, DayOfWeek dayOfWeek) {
        Optional<WorkingHours> workingHoursOptional = hairdresser.getWorkingHoursForDay(dayOfWeek);
        LOGGER.info(workingHoursOptional.toString());

        if (workingHoursOptional.isPresent()) {
            WorkingHours workingHours = workingHoursOptional.get();

            LocalDateTime start = LocalDateTime.of(dateTime.toLocalDate(), workingHours.getStart());
            LocalDateTime end = LocalDateTime.of(dateTime.toLocalDate(), workingHours.getEnd());

            if (end.isBefore(start)) {
                end = end.plusDays(1);
            }

            while (!start.plusMinutes(ONE_SERVICE_TIME).isAfter(end)) {
                timeSlots.add(new TimeSlot(start, start.plusMinutes(ONE_SERVICE_TIME), null));
                start = start.plusMinutes(ONE_SERVICE_TIME);
            }
        }
    }

}