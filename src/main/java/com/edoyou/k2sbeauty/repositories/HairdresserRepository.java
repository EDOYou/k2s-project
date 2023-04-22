package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HairdresserRepository extends JpaRepository<Hairdresser, Long> {
    List<Hairdresser> findBySpecialization(String specialization);
}
