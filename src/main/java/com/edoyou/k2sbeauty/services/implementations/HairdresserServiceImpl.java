package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * HairdresserServiceImpl is the implementation of the {@link com.edoyou.k2sbeauty.services.interfaces.HairdresserService} interface.
 * This class provides the necessary functionality for handling Hairdresser related operations
 * such as finding hairdressers by specialization.
 *
 * @see Hairdresser
 */
@Service
public class HairdresserServiceImpl extends UserServiceImpl implements HairdresserService {

  private final HairdresserRepository hairdresserRepository;

  @Autowired
  public HairdresserServiceImpl(UserRepository userRepository, HairdresserRepository hairdresserRepository) {
    super(userRepository);
    this.hairdresserRepository = hairdresserRepository;
  }

  @Override
  public List<Hairdresser> findBySpecialization(String specialization) {
    return hairdresserRepository.findBySpecialization(specialization);
  }

  @Override
  public List<Hairdresser> findAllHairdressers(String sortBy) {
    if (sortBy == null) {
      return hairdresserRepository.findAll();
    }

    Sort sort;
    if ("lastName".equalsIgnoreCase(sortBy)) {
      sort = Sort.by(Sort.Direction.ASC, "lastName");
    } else if ("rating".equalsIgnoreCase(sortBy)) {
      sort = Sort.by(Sort.Direction.DESC, "rating");
    } else {
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }

    return hairdresserRepository.findAll(sort);
  }

  @Override
  public Hairdresser updateHairdresser(Hairdresser hairdresserDetails) {
    if (hairdresserDetails.getId() == null) {
      throw new IllegalArgumentException("Hairdresser ID cannot be null.");
    }

    Hairdresser existingHairdresser = hairdresserRepository.findById(hairdresserDetails.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Hairdresser not found with ID: " + hairdresserDetails.getId()));

    existingHairdresser.setFirstName(hairdresserDetails.getFirstName());
    existingHairdresser.setLastName(hairdresserDetails.getLastName());
    existingHairdresser.setAppointments(hairdresserDetails.getAppointments());
    existingHairdresser.setBeautyServices(hairdresserDetails.getBeautyServices());
    existingHairdresser.setSpecialization(hairdresserDetails.getSpecialization());
    existingHairdresser.setEmail(hairdresserDetails.getEmail());
    existingHairdresser.setPassword(hairdresserDetails.getPassword());
    existingHairdresser.setPhone(hairdresserDetails.getPhone());

    return hairdresserRepository.save(existingHairdresser);
  }

  @Override
  public void deleteHairdresser(Long id) {
    Hairdresser hairdresserToDelete = hairdresserRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hairdresser not found with ID: " + id));

    hairdresserRepository.delete(hairdresserToDelete);
  }
}