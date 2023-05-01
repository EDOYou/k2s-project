package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  List<Appointment> findByClient(Client client);

  List<Appointment> findByHairdresser(Hairdresser hairdresser);

  List<Appointment> findByBeautyService(BeautyService beautyService);

  List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

  List<Appointment> findByClientAndHairdresser(Client client, Hairdresser hairdresser);
}
