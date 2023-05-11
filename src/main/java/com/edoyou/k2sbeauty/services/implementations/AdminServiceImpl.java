package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.AppointmentNotFoundException;
import com.edoyou.k2sbeauty.exceptions.UserNotFoundException;
import com.edoyou.k2sbeauty.services.interfaces.AdminService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.UserService;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

  private final AppointmentService appointmentService;
  private final UserService userService;

  private static final Logger LOGGER = Logger.getLogger(AdminServiceImpl.class.getName());

  @Autowired
  public AdminServiceImpl(AppointmentService appointmentService,
      @Qualifier("userServiceImpl") UserService userService) {
    this.appointmentService = appointmentService;
    this.userService = userService;
  }

  @Override
  public List<Appointment> getAllAppointments() {
    LOGGER.info("Fetching all appointments");
    try {
      return appointmentService.findAllAppointments();
    } catch (Exception e) {
      LOGGER.severe("Error while fetching all appointments " + e);
      throw new RuntimeException("Error while fetching all appointments", e);
    }
  }

  @Override
  public Appointment updateAppointmentStatus(Long appointmentId, String status) {
    LOGGER.info("Updating appointment status for appointmentId: {" + appointmentId + "}");
    try {
      Appointment appointment = appointmentService.findById(appointmentId)
          .orElseThrow(() -> new AppointmentNotFoundException(
              "Appointment with id " + appointmentId + " does not exist."));
      appointmentService.updatePaymentStatus(appointmentId, PaymentStatus.PAID);
      return appointmentService.saveAppointment(appointment);
    } catch (Exception e) {
      LOGGER.severe("Error while updating appointment status " + e);
      throw new RuntimeException("Error while updating appointment status", e);
    }
  }

  @Override
  public List<User> getAllUsers() {
    LOGGER.info("Fetching all users");
    try {
      return userService.findAllUsers();
    } catch (Exception e) {
      LOGGER.severe("Error while fetching all users" + e);
      throw new RuntimeException("Error while fetching all users", e);
    }
  }

  @Override
  public User updateUserRole(Long userId, String roleName) {
    LOGGER.info("Updating user role for userId: {" + userId + "}");
    try {
      User user = userService.findUserById(userId)
          .orElseThrow(
              () -> new UserNotFoundException("User with id " + userId + " does not exist."));
      userService.updateRole(user, roleName);
      return userService.saveUser(user);
    } catch (Exception e) {
      LOGGER.severe("Error while updating user role " + e);
      throw new RuntimeException("Error while updating user role", e);
    }
  }
}
