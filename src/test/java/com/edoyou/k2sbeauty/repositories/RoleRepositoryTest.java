package com.edoyou.k2sbeauty.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.edoyou.k2sbeauty.entities.model.Role;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class RoleRepositoryTest {

  @Autowired
  private RoleRepository roleRepository;
  private Role role;

  @BeforeEach
  void setUp() {
    role = new Role();
    role.setName("Barber");
  }


  @Test
  public void shouldSaveRole() {
    Role savedRole = roleRepository.save(role);

    assertThat(savedRole).isNotNull();
    assertThat(savedRole.getId()).isNotNull();
  }

  @Test
  public void shouldFindRoleByName() {
    roleRepository.save(role);

    Optional<Role> foundRole = roleRepository.findByName("Barber");
    assertThat(foundRole).isPresent();
    assertThat(foundRole.get().getName()).isEqualTo(role.getName());
  }

}
