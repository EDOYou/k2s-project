package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.services.implementations.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

  @InjectMocks
  private RoleServiceImpl roleService;

  @Mock
  private RoleRepository roleRepository;

  private Role role;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    role = new Role();
    role.setId(1L);
    role.setName("ROLE_USER");
  }

  @Test
  void getRoleByName() {
    when(roleRepository.findByName(any(String.class))).thenReturn(Optional.of(role));

    Optional<Role> foundRole = roleService.getRoleByName(role.getName());

    assertTrue(foundRole.isPresent());
    assertEquals(role.getId(), foundRole.get().getId());
    assertEquals(role.getName(), foundRole.get().getName());
  }

}