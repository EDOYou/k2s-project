package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> { }