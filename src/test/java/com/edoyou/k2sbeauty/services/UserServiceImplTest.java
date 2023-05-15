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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
  void shouldFindUserByEmail() {
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findUserByEmail(user.getEmail());

    assertThat(foundUser.isPresent()).isTrue();
    assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
  }

}