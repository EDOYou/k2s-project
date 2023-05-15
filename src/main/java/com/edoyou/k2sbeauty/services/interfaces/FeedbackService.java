package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import java.util.List;
import java.util.Optional;

public interface FeedbackService {

  Feedback createFeedback(Feedback feedback);

  Optional<Feedback> getFeedbackById(Long id);

  List<Feedback> getAllFeedback();

  Feedback updateFeedback(Feedback feedback);

  double getAverageRatingForClient(Long clientId);
}