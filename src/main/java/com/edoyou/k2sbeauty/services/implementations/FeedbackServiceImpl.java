package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.FeedbackRepository;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

  private static final Logger LOGGER = LogManager.getLogger(FeedbackServiceImpl.class.getName());
  private final FeedbackRepository feedbackRepository;
  private final HairdresserService hairdresserService;

  @Autowired
  public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
      @Lazy HairdresserService hairdresserService) {
    this.feedbackRepository = feedbackRepository;
    this.hairdresserService = hairdresserService;
  }

  /**
   * Creates a new feedback.
   *
   * @param feedback The feedback object to create.
   */
  @Override
  public void createFeedback(Feedback feedback) {
    LOGGER.info("Creating feedback ...");
    Feedback savedFeedback = feedbackRepository.save(feedback);
    Hairdresser hairdresser = savedFeedback.getAppointment().getHairdresser();
    hairdresserService.updateRating(hairdresser);
  }

  /**
   * Retrieves a feedback by ID.
   *
   * @param id The ID of the feedback to retrieve.
   * @return An Optional containing the feedback if found, or empty if not found.
   */
  @Override
  public Optional<Feedback> getFeedbackById(Long id) {
    LOGGER.info("Getting feedback by ID ...");
    return feedbackRepository.findById(id);
  }
}