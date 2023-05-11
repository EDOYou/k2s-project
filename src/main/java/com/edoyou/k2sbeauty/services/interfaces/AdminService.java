package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.User;
import java.util.List;

public interface AdminService {

  List<Appointment> getAllAppointments();

  Appointment updateAppointmentStatus(Long appointmentId, String status);

  List<User> getAllUsers();

  User updateUserRole(Long userId, String roleName);
}