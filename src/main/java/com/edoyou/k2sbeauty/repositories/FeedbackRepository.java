package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The {@code FeedbackRepository} interface extends {@link JpaRepository} and provides methods for
 * querying the {@link Feedback} entity. This repository enables the retrieval and manipulation of
 * feedback-related data from the database.
 *
 * @see Feedback
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}