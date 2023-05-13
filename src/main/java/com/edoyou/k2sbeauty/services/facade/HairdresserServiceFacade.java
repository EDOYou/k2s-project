package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HairdresserServiceFacade {

  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final BeautyServiceService beautyServiceService;
  private final HairdresserService hairdresserService;

  @Autowired
  public HairdresserServiceFacade(PasswordEncoder passwordEncoder, RoleService roleService,
      BeautyServiceService beautyServiceService, HairdresserService hairdresserService) {
    this.passwordEncoder = passwordEncoder;
    this.roleService = roleService;
    this.beautyServiceService = beautyServiceService;
    this.hairdresserService = hairdresserService;
  }

  public void registerHairdresser(Hairdresser hairdresser) {
    hairdresser.setPassword(passwordEncoder.encode(hairdresser.getPassword()));
    hairdresser.setRoles(Set.of(roleService.getRoleByName("ROLE_HAIRDRESSER").orElseThrow()));

    List<BeautyService> services = beautyServiceService.findAllByIdIn(
        hairdresser.getSelectedServiceIds());
    hairdresser.setBeautyServices(new HashSet<>(services));

    hairdresserService.saveHairdresser(hairdresser);
  }
}