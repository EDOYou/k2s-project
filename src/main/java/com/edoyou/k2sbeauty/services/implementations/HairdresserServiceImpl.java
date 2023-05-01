package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
