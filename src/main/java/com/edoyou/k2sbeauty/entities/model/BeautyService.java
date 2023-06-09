package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The BeautyService entity class represents a beauty service provided by the salon.
 * It contains details about the service like name, description, duration, and price.
 */
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
  private int duration;

  @Column(nullable = false)
  private double price;

  @ManyToMany(mappedBy = "beautyServices", fetch = FetchType.EAGER)
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

  public void setDuration(int duration) {
    this.duration = duration;
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

  public int getDuration() {
    return duration;
  }

  @Override
  public String toString() {
    return "BeautyService{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", price=" + price +
        '}';
  }
}