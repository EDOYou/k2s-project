package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.Feedback;
import java.util.Optional;

public interface FeedbackService {

  void createFeedback(Feedback feedback);

  Optional<Feedback> getFeedbackById(Long id);
}