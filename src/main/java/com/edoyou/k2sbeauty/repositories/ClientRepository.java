package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  Client findByAppointments(Appointment appointment);
  List<Client> findByAppointmentsHairdresser(Hairdresser hairdresser);
}