package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.WorkingHoursRepository;
import com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkingHoursServiceImpl implements WorkingHoursService {

  private static final Logger LOGGER = Logger.getLogger(WorkingHoursServiceImpl.class.getName());
  private final WorkingHoursRepository workingHoursRepository;

  @Autowired
  public WorkingHoursServiceImpl(WorkingHoursRepository workingHoursRepository) {
    this.workingHoursRepository = workingHoursRepository;
  }

  @Override
  public WorkingHours saveWorkingHours(WorkingHours workingHours) {
    LOGGER.info("Saving working hours...");
    return workingHoursRepository.save(workingHours);
  }

  @Override
  public void deleteWorkingHours(Long id) {
    LOGGER.info("Deleting working hours...");
    workingHoursRepository.deleteById(id);
  }

  @Override
  public List<WorkingHours> findAllWorkingHours() {
    LOGGER.info("Finding working hours...");
    return workingHoursRepository.findAll();
  }

  @Override
  public WorkingHours findWorkingHoursById(Long id) {
    LOGGER.info("Finding working hours by ID...");
    return workingHoursRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("WorkingHours " + id));
  }

  public Optional<WorkingHours> findByDayOfWeekAndStartAndEnd(DayOfWeek dayOfWeek, LocalTime start, LocalTime end) {
    LOGGER.info("Find By Day Of Week And Start And End..");
    return workingHoursRepository.findByDayOfWeekAndStartAndEnd(dayOfWeek, start, end);
  }
}