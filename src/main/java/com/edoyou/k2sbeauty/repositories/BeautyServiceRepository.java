package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {

  List<BeautyService> findByNameContainingIgnoreCase(String name);

  List<BeautyService> findByPriceBetween(Double minPrice, Double maxPrice);
}
