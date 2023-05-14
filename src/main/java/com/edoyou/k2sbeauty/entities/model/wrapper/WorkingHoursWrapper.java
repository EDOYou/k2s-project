package com.edoyou.k2sbeauty.entities.model.wrapper;

import com.edoyou.k2sbeauty.annotation.AtLeastOneDay;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import java.util.HashMap;
import java.util.Map;

@AtLeastOneDay
public class WorkingHoursWrapper {

  private Map<Integer, WorkingHoursDTO> workingHours = new HashMap<>();

  public Map<Integer, WorkingHoursDTO> getWorkingHoursMap() {
    return workingHours;
  }

  public void setWorkingHoursMap(Map<Integer, WorkingHoursDTO> workingHours) {
    this.workingHours = workingHours;
  }
}