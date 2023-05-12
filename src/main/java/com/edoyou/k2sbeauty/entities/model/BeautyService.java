package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service")
public class BeautyService {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private double price;

  @ManyToMany(mappedBy = "beautyServices")
  private Set<Hairdresser> hairdressers = new HashSet<>();

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setHairdressers(Set<Hairdresser> hairdressers) {
    this.hairdressers = hairdressers;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public double getPrice() {
    return price;
  }

  public Set<Hairdresser> getHairdressers() {
    return hairdressers;
  }
}