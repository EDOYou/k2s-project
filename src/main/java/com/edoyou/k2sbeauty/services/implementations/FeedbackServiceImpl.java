package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.FeedbackRepository;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service implementation for managing feedback in the application. Provides methods for creating,
 * retrieving, updating, and deleting feedback, as well as retrieving feedback by client ID.
 *
 * @see Feedback
 * @see FeedbackRepository
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final HairdresserService hairdresserService;

  @Autowired
  public FeedbackServiceImpl(FeedbackRepository feedbackRepository, @Lazy HairdresserService hairdresserService) {
    this.feedbackRepository = feedbackRepository;
    this.hairdresserService = hairdresserService;
  }

  /**
   * Creates a new feedback.
   *
   * @param feedback The feedback object to create.
   * @return The created feedback object.
   */
  @Override
  public Feedback createFeedback(Feedback feedback) {
    Feedback savedFeedback = feedbackRepository.save(feedback);
    Hairdresser hairdresser = savedFeedback.getAppointment().getHairdresser();
    hairdresserService.updateRating(hairdresser);
    return savedFeedback;
  }

  /**
   * Retrieves a feedback by ID.
   *
   * @param id The ID of the feedback to retrieve.
   * @return An Optional containing the feedback if found, or empty if not found.
   */
  @Override
  public Optional<Feedback> getFeedbackById(Long id) {
    return feedbackRepository.findById(id);
  }
}