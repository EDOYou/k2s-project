package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
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
    user.setEmail("test@test.com");
    user.setFirstName("Test");
    user.setLastName("User");
    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    when(userRepository.findByEmail("invalid@test.com")).thenReturn(Optional.empty());
  }

  @Test
  public void whenValidEmail_thenUserShouldBeFound() {
    String email = "test@test.com";
    Optional<User> found = userService.findUserByEmail(email);

    assertTrue(found.isPresent());
    assertThat(found.get().getEmail()).isEqualTo(email);
  }

  @Test
  public void whenInValidEmail_thenUserShouldNotBeFound() {
    String email = "invalid@test.com";
    Optional<User> found = userService.findUserByEmail(email);

    assertFalse(found.isPresent());
  }

  @Test
  void shouldFindUserByEmail() {
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findUserByEmail(user.getEmail());

    assertThat(foundUser.isPresent()).isTrue();
    assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  public void testFindUserByEmailIsCalledOnce() {
    String email = "test@test.com";
    userService.findUserByEmail(email);

    verify(userRepository, times(1)).findByEmail(email);
  }

}