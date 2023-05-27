package com.edoyou.k2sbeauty.dto;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import java.util.List;

public record AppointmentDTO(List<Appointment> completedAppointments,
                             List<Appointment> pendingAppointments) {

}