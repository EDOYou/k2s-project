package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HairdresserServiceFacade {

  public static final Logger LOGGER = Logger.getLogger(HairdresserServiceFacade.class.getName());
  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final BeautyServiceService beautyServiceService;
  private final HairdresserService hairdresserService;
  private final AppointmentService appointmentService;
  private final WorkingHoursService workingHoursService;


  @Autowired
  public HairdresserServiceFacade(PasswordEncoder passwordEncoder, RoleService roleService,
      BeautyServiceService beautyServiceService, HairdresserService hairdresserService,
      AppointmentService appointmentService, WorkingHoursService workingHoursService) {
    this.passwordEncoder = passwordEncoder;
    this.roleService = roleService;
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
    this.appointmentService = appointmentService;
    this.workingHoursService = workingHoursService;
  }

  //@Transactional
  public void registerHairdresser(Hairdresser hairdresser,
      Map<Integer, WorkingHoursDTO> workingHoursDtoMap) {

    hairdresser.setPassword(passwordEncoder.encode(hairdresser.getPassword()));
    hairdresser.setRoles(Set.of(roleService.getRoleByName("ROLE_HAIRDRESSER").orElseThrow()));

    List<BeautyService> services = beautyServiceService.findAllByIdIn(
        hairdresser.getSelectedServiceIds());
    hairdresser.setBeautyServices(new HashSet<>(services));

    for (Map.Entry<Integer, WorkingHoursDTO> entry : workingHoursDtoMap.entrySet()) {
      if (entry.getValue().getStart() != null && entry.getValue().getEnd() != null) {

        WorkingHours workingHours = findOrCreateWorkingHours(entry.getKey(), entry.getValue());

        workingHours.getHairdressers().add(hairdresser);
        hairdresser.getWorkingHours().add(workingHours);

        workingHoursService.saveWorkingHours(workingHours);
        hairdresserService.saveHairdresser(hairdresser);
      }
    }

  }

  private WorkingHours findOrCreateWorkingHours(Integer dayOfWeekIndex,
      WorkingHoursDTO workingHoursDto) {
    Optional<WorkingHours> existingWorkingHours = workingHoursService.findByDayOfWeekAndStartAndEnd(
        DayOfWeek.values()[dayOfWeekIndex], workingHoursDto.getStart(), workingHoursDto.getEnd());

    if (existingWorkingHours.isPresent()) {
      return existingWorkingHours.get();
    } else {
      WorkingHours workingHours = new WorkingHours();
      workingHours.setDayOfWeek(DayOfWeek.values()[dayOfWeekIndex]);
      workingHours.setStart(workingHoursDto.getStart());
      workingHours.setEnd(workingHoursDto.getEnd());
      return workingHours;
    }
  }

  public void completeAppointment(Long id) {
    Optional<Appointment> appointmentOptional = appointmentService.findById(id);
    if (appointmentOptional.isEmpty()) {
      LOGGER.info("No appointment found with ID: " + id);
      return;
    }

    Appointment appointment = appointmentOptional.get();
    appointment.setCompleted(true);
    // Update function is used of the save() method here
    appointmentService.saveAppointment(appointment);
  }

}