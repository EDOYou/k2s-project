package com.edoyou.k2sbeauty.entities.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

/**
 * The Role entity represents a role that a {@link User} can have in the system.
 * <p>
 * Each Role is linked with multiple users, representing that the users have the role.
 * Each Role has a unique name.
 * </p>
 */
@Entity
@Table(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private Set<User> users = new HashSet<>();

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Set<User> getUsers() {
    return users;
  }
}
