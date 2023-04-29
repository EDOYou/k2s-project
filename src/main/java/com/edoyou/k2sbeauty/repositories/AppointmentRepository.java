package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Service;
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
