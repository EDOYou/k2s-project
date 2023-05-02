package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.implementations.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

  @InjectMocks
  private RoleServiceImpl roleService;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private UserRepository userRepository;

  private Role role;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    role = new Role();
    role.setId(1L);
    role.setName("ROLE_USER");
  }

  @Test
  void assignRoleToUser() {
    User user = new User();
    user.setId(1L);

    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
    when(roleRepository.findById(any(Long.class))).thenReturn(Optional.of(role));
    when(userRepository.save(any(User.class))).thenReturn(user);

    User updatedUser = roleService.assignRoleToUser(user.getId(), role.getId());

    assertNotNull(updatedUser);
    assertEquals(user.getId(), updatedUser.getId());
    assertTrue(updatedUser.getRoles().contains(role));
  }

  @Test
  void createRole() {
    when(roleRepository.findByName(any(String.class))).thenReturn(Optional.empty());
    when(roleRepository.save(any(Role.class))).thenReturn(role);

    Role createdRole = roleService.createRole(role);

    assertNotNull(createdRole);
    assertEquals(role.getId(), createdRole.getId());
    assertEquals(role.getName(), createdRole.getName());
  }

  @Test
  void createRole_alreadyExists() {
    when(roleRepository.findByName(any(String.class))).thenReturn(Optional.of(role));

    assertThrows(IllegalStateException.class, () -> roleService.createRole(role));
  }

  @Test
  void getRoleById() {
    when(roleRepository.findById(any(Long.class))).thenReturn(Optional.of(role));

    Optional<Role> foundRole = roleService.getRoleById(role.getId());

    assertTrue(foundRole.isPresent());
    assertEquals(role.getId(), foundRole.get().getId());
    assertEquals(role.getName(), foundRole.get().getName());
  }

  @Test
  void getRoleByName() {
    when(roleRepository.findByName(any(String.class))).thenReturn(Optional.of(role));

    Optional<Role> foundRole = roleService.getRoleByName(role.getName());

    assertTrue(foundRole.isPresent());
    assertEquals(role.getId(), foundRole.get().getId());
    assertEquals(role.getName(), foundRole.get().getName());
  }

  @Test
  void getAllRoles() {
    List<Role> roles = new ArrayList<>();
    roles.add(role);

    when(roleRepository.findAll()).thenReturn(roles);

    List<Role> foundRoles = roleService.getAllRoles();

    assertEquals(roles.size(), foundRoles.size());
    assertEquals(roles.get(0).getId(), foundRoles.get(0).getId());
    assertEquals(roles.get(0).getName(), foundRoles.get(0).getName());
  }

  @Test
  void updateRole() {
    when(roleRepository.existsById(any(Long.class))).thenReturn(true);
    when(roleRepository.save(any(Role.class))).thenReturn(role);

    Role updatedRole = roleService.updateRole(role);

    assertNotNull(updatedRole);
    assertEquals(role.getId(), updatedRole.getId());
    assertEquals(role.getName(), updatedRole.getName());
  }

  @Test
  void updateRole_notFound() {
    when(roleRepository.existsById(any(Long.class))).thenReturn(false);

    assertThrows(IllegalStateException.class, () -> roleService.updateRole(role));
  }

  @Test
  void deleteRoleById() {
    when(roleRepository.existsById(any(Long.class))).thenReturn(true);
    doNothing().when(roleRepository).deleteById(any(Long.class));

    roleService.deleteRoleById(role.getId());

    verify(roleRepository, times(1)).deleteById(any(Long.class));
  }

  @Test
  void deleteRoleById_notFound() {
    when(roleRepository.existsById(any(Long.class))).thenReturn(false);

    assertThrows(IllegalStateException.class, () -> roleService.deleteRoleById(role.getId()));
  }
}