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

@Service
public class WorkingHoursServiceImpl implements WorkingHoursService {

  private static final Logger LOGGER = LogManager.getLogger(
      WorkingHoursServiceImpl.class.getName());
  private final WorkingHoursRepository workingHoursRepository;

  @Autowired
  public WorkingHoursServiceImpl(WorkingHoursRepository workingHoursRepository) {
    this.workingHoursRepository = workingHoursRepository;
  }

  @Override
  public void saveWorkingHours(WorkingHours workingHours) {
    LOGGER.info("Saving working hours...");
    workingHoursRepository.save(workingHours);
  }

  @Override
  public void deleteWorkingHours(Long id) {
    LOGGER.info("Deleting working hours...");
    workingHoursRepository.deleteById(id);
  }

  public Optional<WorkingHours> findByDayOfWeekAndStartAndEnd(DayOfWeek dayOfWeek, LocalTime start,
      LocalTime end) {
    LOGGER.info("Find By Day Of Week And Start And End..");
    return workingHoursRepository.findByDayOfWeekAndStartAndEnd(dayOfWeek, start, end);
  }
}