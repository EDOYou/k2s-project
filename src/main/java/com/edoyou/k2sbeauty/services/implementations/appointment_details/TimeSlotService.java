package com.edoyou.k2sbeauty.services.implementations.appointment_details;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TimeSlotService {
  public List<TimeSlot> generateTimeSlots(Hairdresser hairdresser) {
    List<TimeSlot> timeSlots = new ArrayList<>();

    LocalDateTime now = LocalDateTime.now();

    for (int i = 0; i < 7; i++) {
      LocalDateTime dateTime = now.plusDays(i);
      DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

      Optional<WorkingHours> workingHoursOptional = hairdresser.getWorkingHoursForDay(dayOfWeek);

      if (workingHoursOptional.isPresent()) {
        WorkingHours workingHours = workingHoursOptional.get();

        LocalDateTime start = LocalDateTime.of(dateTime.toLocalDate(), workingHours.getStart());
        LocalDateTime end = LocalDateTime.of(dateTime.toLocalDate(), workingHours.getEnd());

        while (start.plusMinutes(90).isBefore(end)) {
          timeSlots.add(new TimeSlot(start, start.plusMinutes(90), null));
          start = start.plusMinutes(90);
        }
      }
    }

    return timeSlots;
  }

}
