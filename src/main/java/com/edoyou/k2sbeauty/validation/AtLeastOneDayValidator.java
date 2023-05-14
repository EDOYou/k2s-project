package com.edoyou.k2sbeauty.validation;

import com.edoyou.k2sbeauty.annotation.AtLeastOneDay;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import com.edoyou.k2sbeauty.entities.model.wrapper.WorkingHoursWrapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.logging.Logger;

public class AtLeastOneDayValidator implements
    ConstraintValidator<AtLeastOneDay, WorkingHoursWrapper> {

  private final static Logger LOGGER = Logger.getLogger(AtLeastOneDayValidator.class.getName());

  @Override
  public boolean isValid(WorkingHoursWrapper workingHoursWrapper,
      ConstraintValidatorContext context) {
    Map<Integer, WorkingHoursDTO> workingHoursDtoMap = workingHoursWrapper.getWorkingHoursMap();

    LOGGER.info("WorkingHoursDtoMap: " + workingHoursDtoMap);

    if (workingHoursDtoMap == null) {
      return false;
    }
    return workingHoursDtoMap.values().stream().anyMatch(workingHoursDto ->
        workingHoursDto.getStart() != null && workingHoursDto.getEnd() != null);
  }
}