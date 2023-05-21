package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.repositories.WorkingHoursRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkingHoursServiceImplTest {

  @Mock
  private WorkingHoursRepository workingHoursRepository;

  @InjectMocks
  private WorkingHoursServiceImpl workingHoursService;

  @Test
  public void saveWorkingHours_test() {
    WorkingHours workingHours = createSampleWorkingHours();

    when(workingHoursRepository.save(any(WorkingHours.class))).thenReturn(workingHours);

    workingHoursService.saveWorkingHours(workingHours);

    verify(workingHoursRepository, times(1)).save(workingHours);
  }

  @Test
  public void deleteWorkingHours_test() {
    Long id = 1L;

    doNothing().when(workingHoursRepository).deleteById(id);

    workingHoursService.deleteWorkingHours(id);

    verify(workingHoursRepository, times(1)).deleteById(id);
  }

  @Test
  public void findByDayOfWeekAndStartAndEnd_test() {
    DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
    LocalTime start = LocalTime.of(8, 0);
    LocalTime end = LocalTime.of(17, 0);
    WorkingHours workingHours = createSampleWorkingHours();

    when(workingHoursRepository.findByDayOfWeekAndStartAndEnd(dayOfWeek, start, end)).thenReturn(
        Optional.of(workingHours));

    Optional<WorkingHours> foundWorkingHours = workingHoursService.findByDayOfWeekAndStartAndEnd(
        dayOfWeek, start, end);

    verify(workingHoursRepository, times(1)).findByDayOfWeekAndStartAndEnd(dayOfWeek, start, end);
    assertEquals(workingHours, foundWorkingHours.orElse(null));
  }

  private WorkingHours createSampleWorkingHours() {
    WorkingHours workingHours = new WorkingHours();
    workingHours.setDayOfWeek(DayOfWeek.MONDAY);
    workingHours.setStart(LocalTime.of(8, 0));
    workingHours.setEnd(LocalTime.of(17, 0));
    return workingHours;
  }
}