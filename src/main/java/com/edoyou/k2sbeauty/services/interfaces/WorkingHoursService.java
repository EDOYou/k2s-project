package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface WorkingHoursService {

  WorkingHours saveWorkingHours(WorkingHours workingHours);

  void deleteWorkingHours(Long id);

  List<WorkingHours> findAllWorkingHours();

  WorkingHours findWorkingHoursById(Long id);

  Optional<WorkingHours> findByDayOfWeekAndStartAndEnd(DayOfWeek value, LocalTime start,
      LocalTime end);
}