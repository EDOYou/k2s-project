package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.exceptions.RoleNotFoundException;
import com.edoyou.k2sbeauty.services.implementations.NotificationService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceFacade {

  private static final Logger LOGGER = LogManager.getLogger(AdminServiceFacade.class.getName());
  private final AppointmentService appointmentService;
  private final HairdresserService hairdresserService;
  private final BeautyServiceService beautyServiceService;
  private final RoleService roleService;
  private final NotificationService notificationService;
  private final WorkingHoursService workingHoursService;
  private final EntityManager entityManager;

  @Autowired
  public AdminServiceFacade(AppointmentService appointmentService,
      HairdresserService hairdresserService,
      BeautyServiceService beautyServiceService,
      RoleService roleService,
      NotificationService notificationService,
      WorkingHoursService workingHoursService,
      EntityManager entityManager) {
    this.appointmentService = appointmentService;
    this.hairdresserService = hairdresserService;
    this.beautyServiceService = beautyServiceService;
    this.roleService = roleService;
    this.notificationService = notificationService;
    this.workingHoursService = workingHoursService;
    this.entityManager = entityManager;
  }

  public Page<Appointment> findAllAppointments(Pageable pageable) {
    LOGGER.info("Find all appointments in Admin Facade ...");
    return this.appointmentService.findAllAppointments(pageable);
  }

  public void changeTimeSlot(Long appointmentId, String newTimeSlot) {
    LOGGER.info("Change the appointment's timeslot ...");
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime newAppointmentTime = LocalDateTime.parse(newTimeSlot, formatter);
    Appointment appointment = appointmentService.findById(appointmentId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Appointment not found for this id :: " + appointmentId));

    appointment.setAppointmentTime(newAppointmentTime);
    appointmentService.updateAppointment(appointmentId, appointment);
  }

  public void updatePaymentStatus(Long appointmentId) {
    LOGGER.info("Updating the payment status of an appointment ...");
    PaymentStatus status = PaymentStatus.fromString("PAID", PaymentStatus.PENDING);
    appointmentService.updatePaymentStatus(appointmentId, status);
  }

  public void cancelAppointment(Long appointmentId) {
    LOGGER.info("Admin cancels the appointment ...");
    appointmentService.deleteAppointment(appointmentId, "ROLE_ADMIN");
  }

  @Transactional
  public void approveHairdresser(Long hairdresserId) {
    LOGGER.info("Admin employs the appointment ...");
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

    if (hairdresser == null) {
      throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
    }

    Role hairdresserRole = roleService.getRoleByName("ROLE_HAIRDRESSER")
        .orElseThrow(() -> new RoleNotFoundException("Role not found."));

    hairdresser.getRoles().removeIf(role -> role.getName().equals("ROLE_APPLICANT"));
    hairdresser.getRoles().add(hairdresserRole);
    hairdresser.setApproved(true);
    hairdresserService.saveHairdresser(hairdresser);
    notifyHairdresser(hairdresser);
  }

  @Transactional
  public void rejectHairdresser(Long hairdresserId) {
    LOGGER.info("Admin rejects the application of an hairdresser ...");
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

    if (hairdresser == null) {
      throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
    }

    Set<WorkingHours> workingHoursSet = hairdresser.getWorkingHours();
    notifyHairdresser(hairdresser);
    hairdresserService.deleteHairdresser(hairdresserId);
    entityManager.flush();
    deleteWorkingHours(workingHoursSet);
  }

  public Page<Hairdresser> findAllYetNotApproved(Boolean isApproved, Pageable pageable) {
    LOGGER.info("Display all the hairdressers whose is_approved column is false ...");
    return hairdresserService.findAllHairdressersByApprovalStatus(isApproved, pageable);
  }

  public List<Hairdresser> findAllHairdressers() {
    return hairdresserService.findAllHairdressers();
  }

  public void saveService(BeautyService beautyService) {
    LOGGER.info("Adding the beauty service ...");
    beautyServiceService.saveService(beautyService);
  }

  public List<BeautyService> findAllBeautyServices() {
    return beautyServiceService.findAllServices();
  }

  @Transactional
  public void assignServiceToHairdresser(Long hairdresserId, Long serviceId) {
    LOGGER.info("Assigning the service to the hairdresser ...");
    Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
    if (hairdresser == null) {
      throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
    }
    BeautyService beautyService = beautyServiceService.findById(serviceId).orElseThrow();
    hairdresser.getBeautyServices().add(beautyService);
    hairdresserService.saveHairdresser(hairdresser);
  }

  public Page<Hairdresser> findHairdressersWithServices(Pageable pageable) {
    return hairdresserService.findAllWithBeautyServices(pageable);
  }

  private void deleteWorkingHours(Set<WorkingHours> workingHoursSet) {
    LOGGER.info("Deleting the working hours when hairdresser is rejected ...");
    for (WorkingHours workingHours : workingHoursSet) {
      entityManager.refresh(workingHours);
      if (workingHours.getHairdressers().isEmpty()) {
        workingHoursService.deleteWorkingHours(workingHours.getId());
      }
    }
  }

  private void notifyHairdresser(Hairdresser hairdresser) {
    LOGGER.info("Mail sent to the client !");
    if (hairdresser.isApproved()) {
      String message = "Dear " + hairdresser.getFirstName()
          + ", your application has been approved. Welcome to our team!";
      notificationService.sendNotification(hairdresser.getEmail(), "Application Approved", message);
    } else {
      String message = "Dear " + hairdresser.getFirstName()
          + ", your application has been reviewed and unfortunately, you were not accepted at this time. Please consider applying again in the future.";
      notificationService.sendNotification(hairdresser.getEmail(), "Application Rejected", message);
    }
  }

}