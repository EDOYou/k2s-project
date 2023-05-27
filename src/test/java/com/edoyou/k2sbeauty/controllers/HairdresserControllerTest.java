package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.dto.AppointmentDTO;
import com.edoyou.k2sbeauty.entities.model.*;
import com.edoyou.k2sbeauty.entities.model.appointment_details.TimeSlot;
import com.edoyou.k2sbeauty.entities.model.wrapper.WorkingHoursWrapper;
import com.edoyou.k2sbeauty.services.facade.HairdresserServiceFacade;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HairdresserControllerTest {

  @InjectMocks
  private HairdresserController hairdresserController;

  @Mock
  private HairdresserServiceFacade hairdresserServiceFacade;

  @Mock
  private BeautyServiceService beautyServiceService;

  @Mock
  private BindingResult bindingResultHairdresser, bindingResultWorkingHours;

  @Test
  public void testGetAppointments() {
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn("testName");

    List<Appointment> completedAppointments = new ArrayList<>();
    List<Appointment> pendingAppointments = new ArrayList<>();
    AppointmentDTO appointmentDTO = new AppointmentDTO(completedAppointments, pendingAppointments);

    when(hairdresserServiceFacade.getAppointments("testName")).thenReturn(appointmentDTO);
    Model model = new ExtendedModelMap();

    String result = hairdresserController.getAppointments(auth, model);

    assertEquals("hairdresser/hairdresser_appointments", result);
    assertSame(completedAppointments, model.getAttribute("completedAppointments"));
    assertSame(pendingAppointments, model.getAttribute("pendingAppointments"));
  }

  @Test
  public void testMarkAppointmentAsCompleted() {
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn("testName");

    String result = hairdresserController.markAppointmentAsCompleted(1L, auth);

    assertEquals("redirect:/hairdresser/appointments", result);
    verify(hairdresserServiceFacade, times(1)).completeAppointment(1L, "testName");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetSchedule() {
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn("testName");
    Map<LocalDate, List<TimeSlot>> schedule = mock(Map.class);
    when(hairdresserServiceFacade.getSchedule("testName")).thenReturn(schedule);
    Model model = new ExtendedModelMap();

    String result = hairdresserController.getSchedule(auth, model);

    assertEquals("hairdresser/schedule", result);
    assertSame(schedule, model.getAttribute("schedule"));
  }

  @Test
  public void testProcessRegistrationForm_withErrors() {
    Hairdresser hairdresser = new Hairdresser();
    WorkingHoursWrapper workingHoursWrapper = new WorkingHoursWrapper();
    Model model = new ExtendedModelMap();

    when(bindingResultHairdresser.hasErrors()).thenReturn(true);
    when(bindingResultWorkingHours.hasErrors()).thenReturn(true);
    List<ObjectError> objectErrors = new ArrayList<>();
    objectErrors.add(new ObjectError("error", "error"));

    when(bindingResultHairdresser.getAllErrors()).thenReturn(objectErrors);
    when(bindingResultWorkingHours.getAllErrors()).thenReturn(objectErrors);

    String result = hairdresserController.processRegistrationForm(hairdresser,
        bindingResultHairdresser, workingHoursWrapper, bindingResultWorkingHours, model);

    assertEquals("/hairdresser/register_hairdresser", result);
    assertTrue(((List<?>) Objects.requireNonNull(model.getAttribute("errors"))).contains("error"));
    verify(hairdresserServiceFacade, times(0)).registerHairdresser(hairdresser,
        workingHoursWrapper.getWorkingHoursMap());
  }

  @Test
  public void testProcessRegistrationForm_withoutErrors() {
    Hairdresser hairdresser = new Hairdresser();
    WorkingHoursWrapper workingHoursWrapper = new WorkingHoursWrapper();
    Model model = new ExtendedModelMap();

    when(bindingResultHairdresser.hasErrors()).thenReturn(false);
    when(bindingResultWorkingHours.hasErrors()).thenReturn(false);

    String result = hairdresserController.processRegistrationForm(hairdresser,
        bindingResultHairdresser, workingHoursWrapper, bindingResultWorkingHours, model);

    assertEquals("redirect:home", result);
    verify(hairdresserServiceFacade, times(1)).registerHairdresser(hairdresser,
        workingHoursWrapper.getWorkingHoursMap());
  }

  @Test
  public void testShowRegistrationForm() {
    List<BeautyService> services = new ArrayList<>();
    Model model = new ExtendedModelMap();

    when(beautyServiceService.findAll()).thenReturn(services);

    String result = hairdresserController.showRegistrationForm(model);

    assertEquals("/hairdresser/register_hairdresser", result);
    assertSame(services, model.getAttribute("services"));
    assertTrue(model.getAttribute("hairdresser") instanceof Hairdresser);
    assertTrue(model.getAttribute("workingHours") instanceof WorkingHoursWrapper);
  }

}