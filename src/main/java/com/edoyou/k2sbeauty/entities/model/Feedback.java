package com.edoyou.k2sbeauty.entities.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "feedbacks")
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(nullable = false)
  private Client client;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(nullable = false)
  private Appointment appointment;

  @Column(nullable = false)
  private String comment;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @Min(1)
  @Max(5)
  private Integer rating;

  public void setId(Long id) {
    this.id = id;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public Long getId() {
    return id;
  }

  public Client getClient() {
    return client;
  }

  public String getComment() {
    return comment;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public int getRating() {
    return rating;
  }

  public Appointment getAppointment() {
    return appointment;
  }
}
