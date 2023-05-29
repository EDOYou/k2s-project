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
 * Provides the concrete implementation of the {@code FeedbackService} interface. This service manages
 * operations related to the {@code Feedback} entity.
 *
 * <p>This service is responsible for creating feedback entries and retrieving them by ID. It interacts
 * with the data layer via {@code FeedbackRepository} and uses {@code HairdresserService} to update hairdresser ratings.
 *
 * <p><strong>Note:</strong> The service is annotated with {@code @Service}, allowing it to be automatically
 * detected by Spring's component scanning.
 *
 * <p>Typical usage:
 * <pre>{@code
 * Feedback feedback = new Feedback();
 * // populate feedback object
 * feedbackService.createFeedback(feedback);
 * }</pre>
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private static final Logger LOGGER = LogManager.getLogger(FeedbackServiceImpl.class.getName());
    private final FeedbackRepository feedbackRepository;
    private final HairdresserService hairdresserService;

    /**
     * Constructs a new FeedbackService. This constructor is annotated with {@code @Autowired}, so Spring
     * automatically injects the dependencies.
     *
     * @param feedbackRepository the repository that this service will use for data access
     * @param hairdresserService the service that this service will use for updating hairdresser ratings
     */
    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
                               @Lazy HairdresserService hairdresserService) {
        this.feedbackRepository = feedbackRepository;
        this.hairdresserService = hairdresserService;
    }

    /**
     * Creates a new feedback entry and updates the rating of the associated hairdresser.
     *
     * <p>This method first saves the given feedback into the database. Then it retrieves the hairdresser
     * associated with the appointment in the feedback and updates their rating.
     *
     * <p>The update of the hairdresser's rating is handled by {@code HairdresserService}.
     *
     * @param feedback the feedback to be saved
     * @throws NullPointerException if the given feedback is {@code null}
     */
    @Override
    public void createFeedback(Feedback feedback) {
        LOGGER.info("Creating feedback ...");
        if (feedback == null) {
            throw new NullPointerException("Feedback cannot be null");
        }
        Feedback savedFeedback = feedbackRepository.save(feedback);
        Hairdresser hairdresser = savedFeedback.getAppointment().getHairdresser();
        hairdresserService.updateRating(hairdresser);
    }

    /**
     * Retrieves a feedback entry by its ID.
     *
     * <p>This method attempts to find a feedback entry in the database by the given ID. If a feedback entry
     * with the given ID exists, it is returned wrapped in an {@code Optional}. If no such feedback entry
     * exists, an empty {@code Optional} is returned.
     *
     * @param id the ID of the feedback entry to retrieve
     * @return an {@code Optional} describing the feedback entry, if found, otherwise an empty {@code Optional}
     * @throws IllegalArgumentException if the given ID is {@code null}
     */
    @Override
    public Optional<Feedback> getFeedbackById(Long id) {
        LOGGER.info("Getting feedback by ID ...");
        return feedbackRepository.findById(id);
    }

}