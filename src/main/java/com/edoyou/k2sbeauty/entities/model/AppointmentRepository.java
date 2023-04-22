package com.edoyou.k2sbeauty.entities.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByClient(Client client);
    List<Appointment> findByHairdresser(Hairdresser hairdresser);
    List<Appointment> findByService(Service service);
    List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByClientAndHairdresser(Client client, Hairdresser hairdresser);
}
