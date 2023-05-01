package com.edoyou.k2sbeauty.services;

import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("johndoe@gmail.com");
    user.setPassword("password");
    user.setPhone("1234567890");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void shouldSaveUser() {
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
    when(userRepository.save(user)).thenReturn(user);

    User savedUser = userService.saveUser(user);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isEqualTo(user.getId());
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void shouldNotSaveUserWithExistingEmail() {
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> userService.saveUser(user))
        .withMessage("Email already exists.");

    verify(userRepository, never()).save(user);
  }

  @Test
  void shouldUpdateUser() {
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);

    User updatedUser = userService.updateUser(user);

    assertThat(updatedUser).isNotNull();
    assertThat(updatedUser.getId()).isEqualTo(user.getId());
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void shouldNotUpdateNonExistingUser() {
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> userService.updateUser(user))
        .withMessage("User not found.");

    verify(userRepository, never()).save(user);
  }

  @Test
  void shouldDeleteUser() {
    userService.deleteUser(user.getId());

    verify(userRepository, times(1)).deleteById(user.getId());
  }

  @Test
  void shouldFindUserByEmail() {
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findUserByEmail(user.getEmail());

    assertThat(foundUser.isPresent()).isTrue();
    assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  void shouldFindAllUsers() {
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAll()).thenReturn(users);

    List<User> foundUsers = userService.findAllUsers();

    assertThat(foundUsers).isNotEmpty();
    assertThat(foundUsers.size()).isEqualTo(1);
    assertThat(foundUsers.get(0).getId()).isEqualTo(user.getId());
  }

  @Test
  void shouldFindUserById() {
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findUserById(user.getId());

    assertThat(foundUser.isPresent()).isTrue();
    assertThat(foundUser.get().getId()).isEqualTo(user.getId());
  }

  @Test
  void shouldNotFindNonExistingUserById() {
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

    Optional<User> foundUser = userService.findUserById(user.getId());

    assertThat(foundUser.isPresent()).isFalse();
  }
}

