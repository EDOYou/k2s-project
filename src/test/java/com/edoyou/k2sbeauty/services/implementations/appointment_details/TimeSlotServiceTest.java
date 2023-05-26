package com.edoyou.k2sbeauty.services.implementations.appointment_details;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TimeSlotServiceTest {

  private TimeSlotService timeSlotService;

  @Mock
  private Hairdresser hairdresser;

  @BeforeEach
  void setUp() {
    timeSlotService = new TimeSlotService();
  }

  @Test
  void shouldGenerateTimeSlots() {
    WorkingHours workingHours = new WorkingHours();
    workingHours.setStart(LocalTime.of(9, 0));
    workingHours.setEnd(LocalTime.of(17, 0));
    when(hairdresser.getWorkingHoursForDay(any(DayOfWeek.class))).thenReturn(
        Optional.of(workingHours));

    List<TimeSlot> timeSlots = timeSlotService.generateTimeSlots(hairdresser);

    assertFalse(timeSlots.isEmpty(), "TimeSlots should not be empty");
    timeSlots.forEach(
        timeSlot -> assertEquals(timeSlot.getStart().plusMinutes(TimeSlotService.ONE_SERVICE_TIME),
            timeSlot.getEnd(),
            "Time slot should be " + TimeSlotService.ONE_SERVICE_TIME + " minutes long"));
  }

  @Test
  void shouldGenerateTimeSlotsForDifferentWorkingHours() {
    WorkingHours mondayHours = new WorkingHours();
    mondayHours.setStart(LocalTime.of(9, 0));
    mondayHours.setEnd(LocalTime.of(12, 0));

    WorkingHours tuesdayHours = new WorkingHours();
    tuesdayHours.setStart(LocalTime.of(14, 0));
    tuesdayHours.setEnd(LocalTime.of(18, 0));

    when(hairdresser.getWorkingHoursForDay(DayOfWeek.MONDAY)).thenReturn(Optional.of(mondayHours));
    when(hairdresser.getWorkingHoursForDay(DayOfWeek.TUESDAY)).thenReturn(
        Optional.of(tuesdayHours));
    for (DayOfWeek day : DayOfWeek.values()) {
      if (day != DayOfWeek.MONDAY && day != DayOfWeek.TUESDAY) {
        when(hairdresser.getWorkingHoursForDay(day)).thenReturn(Optional.empty());
      }
    }

    List<TimeSlot> timeSlots = timeSlotService.generateTimeSlots(hairdresser);

    assertTrue(timeSlots.stream().allMatch(
            slot -> (slot.getStart().getHour() >= 9 && slot.getStart().getHour() < 12) || (
                slot.getStart().getHour() >= 14 && slot.getStart().getHour() < 18)),
        "All time slots should be within the hairdresser's working hours");
  }

  @Test
  void shouldGenerateOneTimeSlotWhenWorkingHoursEqualToServiceTime() {
    WorkingHours workingHours = new WorkingHours();
    workingHours.setStart(LocalTime.of(9, 0));
    workingHours.setEnd(LocalTime.of(10, 30));
    when(hairdresser.getWorkingHoursForDay(any(DayOfWeek.class))).thenReturn(
        Optional.of(workingHours));

    List<TimeSlot> timeSlots = timeSlotService.generateTimeSlots(hairdresser);

    assertEquals(7, timeSlots.size(), "Service should generate one time slot per day for 7 days");
  }

  @Test
  void shouldGenerateNoTimeSlotsWhenWorkingHoursLessThanServiceTime() {
    WorkingHours workingHours = new WorkingHours();
    workingHours.setStart(LocalTime.of(9, 0));
    workingHours.setEnd(LocalTime.of(10, 0));
    when(hairdresser.getWorkingHoursForDay(any(DayOfWeek.class))).thenReturn(
        Optional.of(workingHours));

    List<TimeSlot> timeSlots = timeSlotService.generateTimeSlots(hairdresser);

    assertTrue(timeSlots.isEmpty(),
        "Service should generate no time slots when working hours less than service time");
  }

}