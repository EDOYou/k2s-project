package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import java.util.Optional;

/**
 * FeedbackService is an interface that provides the necessary operations for managing {@link Feedback} entities.
 */
public interface FeedbackService {

  /**
   * Persists a new Feedback instance into the repository.
   *
   * @param feedback The Feedback instance to be saved.
   */
  void createFeedback(Feedback feedback);

  /**
   * Retrieves a Feedback entity based on its identifier.
   *
   * @param id The identifier of the Feedback entity.
   * @return An Optional that may contain the found Feedback entity.
   */
  Optional<Feedback> getFeedbackById(Long id);
}