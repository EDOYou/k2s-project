package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.repositories.FeedbackRepository;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // After saving the feedback, update the hairdresser's rating
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

  /**
   * Retrieves all feedback.
   *
   * @return A list of all feedback objects.
   */
  @Override
  public List<Feedback> getAllFeedback() {
    return feedbackRepository.findAll();
  }

  /**
   * Updates a feedback.
   *
   * @param feedback The feedback object to update.
   * @return The updated feedback object.
   * @throws IllegalStateException If the feedback does not exist.
   */
  @Override
  public Feedback updateFeedback(Feedback feedback) {
    if (!feedbackRepository.existsById(feedback.getId())) {
      throw new IllegalStateException("Feedback with id " + feedback.getId() + " does not exist.");
    }
    return feedbackRepository.save(feedback);
  }

  /**
   * Retrieves the average rating for a specific client.
   *
   * @param clientId The ID of the client.
   * @return The average rating for the specified client.
   */
  @Override
  public double getAverageRatingForClient(Long clientId) {
    List<Feedback> feedbackList = feedbackRepository.findByClientId(clientId);
    return feedbackList.stream().mapToInt(Feedback::getRating).average().orElse(0);
  }
}