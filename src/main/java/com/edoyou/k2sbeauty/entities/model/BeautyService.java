package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.*;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hairdresser_id")
  private Hairdresser hairdresser;

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

  public void setHairdresser(Hairdresser hairdresser) {
    this.hairdresser = hairdresser;
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

  public Hairdresser getHairdresser() {
    return hairdresser;
  }
}
