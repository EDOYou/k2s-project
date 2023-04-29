package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

  List<Service> findByNameContainingIgnoreCase(String name);
}
