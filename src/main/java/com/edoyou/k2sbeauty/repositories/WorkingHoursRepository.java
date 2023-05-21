package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

  Optional<WorkingHours> findByDayOfWeekAndStartAndEnd(DayOfWeek dayOfWeek, LocalTime start,
      LocalTime end);
}
