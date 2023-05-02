package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedbackService {

  Feedback createFeedback(Feedback feedback);

  Optional<Feedback> getFeedbackById(Long id);

  List<Feedback> getAllFeedback();

  List<Feedback> getFeedbackByClientId(Long clientId);

  Feedback updateFeedback(Feedback feedback);

  void deleteFeedbackById(Long id);

  List<Feedback> getLatestNFeedbackByClientId(Long clientId, int n);

  List<Feedback> getFeedbackByDateRange(LocalDateTime startDate, LocalDateTime endDate);

  List<Feedback> getFeedbackWithKeywords(String keywords);

  double getAverageRatingForClient(Long clientId);
}