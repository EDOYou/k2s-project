package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.repositories.HairdresserRepository;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import java.util.Collections;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * HairdresserServiceImpl is the implementation of the
 * {@link com.edoyou.k2sbeauty.services.interfaces.HairdresserService} interface. This class
 * provides the necessary functionality for handling Hairdresser related operations such as finding
 * hairdressers by specialization.
 *
 * @see Hairdresser
 */
@Service
public class HairdresserServiceImpl extends UserServiceImpl implements HairdresserService {
  private static final Logger LOGGER = Logger.getLogger(HairdresserServiceImpl.class.getName());

  private final HairdresserRepository hairdresserRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private Hairdresser temporaryRegistration;

  @Autowired
  public HairdresserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
      HairdresserRepository hairdresserRepository,
      PasswordEncoder passwordEncoder) {
    super(userRepository, roleRepository, passwordEncoder);
    this.hairdresserRepository = hairdresserRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }


  @Override
  public Hairdresser saveHairdresser(Hairdresser hairdresser) {
    LOGGER.info("Saving hairdresser.");
    return hairdresserRepository.save(hairdresser);
  }

  @Override
  public List<Hairdresser> findBySpecialization(String specialization) {
    LOGGER.info("Searching hairdresser by specialization.");
    return hairdresserRepository.findBySpecialization(specialization);
  }

  @Override
  public List<Hairdresser> findAllHairdressers(String sortBy) {
    if (sortBy == null) {
      return hairdresserRepository.findAll();
    }

    if ("serviceName".equalsIgnoreCase(sortBy) || "price".equalsIgnoreCase(sortBy)) {
      // Delegate sorting by service name or price to the findAllHairdressersByServiceId method
      return findAllHairdressersByServiceId(sortBy, null);
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
  public List<Hairdresser> findAllHairdressers() {
    return hairdresserRepository.findAll();
  }

  /**
   * Finds all hairdressers filtered by service ID and sorted by the given criteria.
   *
   * @param sortBy    The field to sort hairdressers by.
   * @param serviceId The ID of the beauty service to filter hairdressers by.
   * @return A list of hairdressers sorted by the given criteria and filtered by the given service
   * ID.
   */
  @Override
  public List<Hairdresser> findAllHairdressersByServiceId(String sortBy, Long serviceId) {
    Sort sort;
    if ("serviceName".equalsIgnoreCase(sortBy) || "price".equalsIgnoreCase(sortBy)) {
      sort = createSortByServiceNameAndPrice(sortBy);
    } else {
      sort = createSortBy(sortBy);
    }

    return hairdresserRepository.findAllByService(serviceId, sort);
  }

  @Override
  public List<Hairdresser> findAllWithBeautyServices() {
    LOGGER.info("Saving hairdresser.");
    return hairdresserRepository.findAllWithBeautyServices();
  }

  @Override
  public Hairdresser findById(Long id) {
    return hairdresserRepository.findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Hairdresser with id " + id + " not found"));
  }

  private Sort createSortByServiceNameAndPrice(String sortBy) {
    return switch (sortBy) {
      case "serviceName" -> Sort.by(Sort.Direction.ASC, "s.name");
      case "price" -> Sort.by(Sort.Direction.ASC, "s.price");
      default -> throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    };
  }

  private Sort createSortBy(String sortBy) {
    if ("lastName".equalsIgnoreCase(sortBy)) {
      return Sort.by(Sort.Direction.ASC, "lastName");
    } else if ("rating".equalsIgnoreCase(sortBy)) {
      return Sort.by(Sort.Direction.DESC, "rating");
    } else {
      throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
  }

  @Override
  public void approveHairdresser(Long userId) {
    LOGGER.info("Admin approves hairdresser.");
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    Role hairdresserRole = roleRepository.findByName("ROLE_HAIRDRESSER")
        .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_HAIRDRESSER"));
    user.setRoles(Collections.singleton(hairdresserRole));
    userRepository.save(user);
  }


  @Override
  public Hairdresser updateHairdresser(Hairdresser hairdresserDetails) {
    LOGGER.info("Updating hairdresser.");
    if (hairdresserDetails.getId() == null) {
      throw new IllegalArgumentException("Hairdresser ID cannot be null.");
    }

    Hairdresser existingHairdresser = hairdresserRepository.findById(hairdresserDetails.getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Hairdresser not found with ID: " + hairdresserDetails.getId()));

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
    LOGGER.info("Firing hairdresser.");
    Hairdresser hairdresserToDelete = hairdresserRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hairdresser not found with ID: " + id));

    hairdresserRepository.delete(hairdresserToDelete);
  }

  @Override
  public void saveTemporaryRegistration(Hairdresser hairdresser) {
    LOGGER.info("Saving temporary registration of the hairdresser.");
    temporaryRegistration = hairdresser;
  }

  @Override
  public Hairdresser getTemporaryRegistration() {
    LOGGER.info("Getting temporary registration of the hairdresser " + temporaryRegistration);
    return temporaryRegistration;
  }

  @Override
  public List<Hairdresser> findAllHairdressersByApprovalStatus(boolean isApproved) {
    return hairdresserRepository.findByIsApproved(isApproved);
  }
}