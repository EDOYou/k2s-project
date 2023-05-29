package com.edoyou.k2sbeauty.entities.model.wrapper;

import com.edoyou.k2sbeauty.annotation.AtLeastOneDay;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link WorkingHoursWrapper} class is a wrapper for a map that stores {@link WorkingHoursDTO} objects.
 * <p>
 * Each {@link WorkingHoursDTO} object in the map represents working hours for a specific day of the week.
 * The map's key is the day of the week (represented as an integer), and the value is the corresponding {@link WorkingHoursDTO} object.
 * </p>
 * <p>
 * The class is annotated with {@link AtLeastOneDay}, ensuring that at least one day's working hours are provided.
 * </p>
 */
@AtLeastOneDay
public class WorkingHoursWrapper {

  private final Map<Integer, WorkingHoursDTO> workingHours = new HashMap<>();

  public Map<Integer, WorkingHoursDTO> getWorkingHoursMap() {
    return workingHours;
  }
}