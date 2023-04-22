package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByClientId(Long clientId);
}
