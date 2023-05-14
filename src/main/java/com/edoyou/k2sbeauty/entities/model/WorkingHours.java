package com.edoyou.k2sbeauty.entities.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "working_hours")
public class WorkingHours {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.ORDINAL)
  private DayOfWeek dayOfWeek;

  @Column(nullable = false)
  private LocalTime start;

  @Column(nullable = false)
  private LocalTime end;

  @ManyToMany(mappedBy = "workingHours", fetch = FetchType.EAGER)
  private Set<Hairdresser> hairdressers = new HashSet<>();

  public void setId(Long id) {
    this.id = id;
  }

  public void setDayOfWeek(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public void setStart(LocalTime start) {
    this.start = start;
  }

  public void setEnd(LocalTime end) {
    this.end = end;
  }

  public void setHairdressers(Set<Hairdresser> hairdressers) {
    this.hairdressers = hairdressers;
  }

  public Long getId() {
    return id;
  }

  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  public LocalTime getStart() {
    return start;
  }

  public LocalTime getEnd() {
    return end;
  }

  public Set<Hairdresser> getHairdressers() {
    return hairdressers;
  }

  @Override
  public String toString() {
    return "WorkingHours{" +
        "id=" + id +
        ", dayOfWeek=" + dayOfWeek +
        ", start=" + start +
        ", end=" + end +
        '}';
  }
}