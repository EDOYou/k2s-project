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

@Service
public class TimeSlotService {

  public static final int ONE_SERVICE_TIME = 90;
  private static final int WEEK_DAYS = 7;
  private static final Logger LOGGER = LogManager.getLogger(TimeSlotService.class.getName());

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

  private static void addTimeSlotsForDay(Hairdresser hairdresser, List<TimeSlot> timeSlots,
      LocalDateTime dateTime, DayOfWeek dayOfWeek) {
    Optional<WorkingHours> workingHoursOptional = hairdresser.getWorkingHoursForDay(dayOfWeek);
    LOGGER.info(workingHoursOptional.toString());

    if (workingHoursOptional.isPresent()) {
      WorkingHours workingHours = workingHoursOptional.get();

      LocalDateTime start = LocalDateTime.of(dateTime.toLocalDate(), workingHours.getStart());
      LocalDateTime end = LocalDateTime.of(dateTime.toLocalDate(), workingHours.getEnd());

      // Handling the case where working hours cross midnight
      if (end.isBefore(start)) {
        end = end.plusDays(1);
      }

      // Handling the case where working hours equal to service time
      if (start.plusMinutes(ONE_SERVICE_TIME).equals(end) || start.plusMinutes(ONE_SERVICE_TIME).isBefore(end)) {
        timeSlots.add(new TimeSlot(start, start.plusMinutes(ONE_SERVICE_TIME), null));
      }

      while (start.plusMinutes(ONE_SERVICE_TIME).isAfter(end)) {
        timeSlots.add(new TimeSlot(start, start.plusMinutes(ONE_SERVICE_TIME), null));
        start = start.plusMinutes(ONE_SERVICE_TIME);
      }
    }
  }

}