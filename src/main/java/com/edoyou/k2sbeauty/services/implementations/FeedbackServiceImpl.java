package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.repositories.FeedbackRepository;
import com.edoyou.k2sbeauty.services.interfaces.FeedbackService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  /**
   * Creates a new feedback.
   *
   * @param feedback The feedback object to create.
   * @return The created feedback object.
   */
  @Override
  public Feedback createFeedback(Feedback feedback) {
    feedback.setCreatedAt(LocalDateTime.now());
    return feedbackRepository.save(feedback);
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
   * Retrieves feedback by client ID.
   *
   * @param clientId The ID of the client whose feedback to retrieve.
   * @return A list of feedback objects for the specified client.
   */
  @Override
  public List<Feedback> getFeedbackByClientId(Long clientId) {
    return feedbackRepository.findByClientId(clientId);
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
   * Deletes a feedback by ID.
   *
   * @param id The ID of the feedback to delete.
   * @throws IllegalStateException If the feedback does not exist.
   */
  @Override
  public void deleteFeedbackById(Long id) {
    if (!feedbackRepository.existsById(id)) {
      throw new IllegalStateException("Feedback with id " + id + " does not exist.");
    }
    feedbackRepository.deleteById(id);
  }

  /**
   * Retrieves the latest N feedback for a specific client.
   *
   * @param clientId The ID of the client.
   * @param n        The number of latest feedback to retrieve.
   * @return A list of the latest N feedback objects for the specified client.
   */
  @Override
  public List<Feedback> getLatestNFeedbackByClientId(Long clientId, int n) {
    List<Feedback> feedbackList = feedbackRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    return feedbackList.subList(0, Math.min(n, feedbackList.size()));
  }

  /**
   * Retrieves feedback within a specific date range.
   *
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @return A list of feedback objects within the specified date range.
   */
  @Override
  public List<Feedback> getFeedbackByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    return feedbackRepository.findByCreatedAtBetween(startDate, endDate);
  }

  /**
   * Retrieves feedback containing specific keywords.
   *
   * @param keywords The keywords to search for in the feedback.
   * @return A list of feedback objects containing the specified keywords.
   */
  @Override
  public List<Feedback> getFeedbackWithKeywords(String keywords) {
    return feedbackRepository.findByCommentContainingIgnoreCase(keywords);
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