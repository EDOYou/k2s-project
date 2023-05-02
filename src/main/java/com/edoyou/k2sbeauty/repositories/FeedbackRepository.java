package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

  List<Feedback> findByClientId(Long clientId);

  List<Feedback> findByClientIdOrderByCreatedAtDesc(Long clientId);

  List<Feedback> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  List<Feedback> findByCommentContainingIgnoreCase(String keywords);
}
