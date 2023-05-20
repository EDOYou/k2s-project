//package com.edoyou.k2sbeauty.services;
//
//import com.edoyou.k2sbeauty.entities.model.Client;
//import com.edoyou.k2sbeauty.entities.model.Feedback;
//import com.edoyou.k2sbeauty.repositories.FeedbackRepository;
//import com.edoyou.k2sbeauty.services.implementations.FeedbackServiceImpl;
//import java.util.Collections;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class FeedbackServiceImplTest {
//
//  @Mock
//  private FeedbackRepository feedbackRepository;
//
//  @InjectMocks
//  private FeedbackServiceImpl feedbackService;
//
//  private Feedback feedback;
//  private Client client;
//
//  @BeforeEach
//  void setUp() {
//    client = new Client();
//    client.setId(1L);
//
//    feedback = new Feedback();
//    feedback.setId(1L);
//    feedback.setClient(client);
//    feedback.setComment("Test comment");
//    feedback.setCreatedAt(LocalDateTime.now());
//    feedback.setRating(5);
//  }
//
//  @Test
//  void createFeedbackWithValidData() {
//    when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);
//
//    Feedback createdFeedback = feedbackService.createFeedback(feedback);
//
//    assertNotNull(createdFeedback);
//    assertEquals(feedback.getId(), createdFeedback.getId());
//    assertEquals(feedback.getClient(), createdFeedback.getClient());
//    assertEquals(feedback.getComment(), createdFeedback.getComment());
//    assertEquals(feedback.getRating(), createdFeedback.getRating());
//  }
//
//  @Test
//  void getFeedbackById() {
//    when(feedbackRepository.findById(feedback.getId())).thenReturn(Optional.of(feedback));
//
//    Optional<Feedback> retrievedFeedback = feedbackService.getFeedbackById(feedback.getId());
//
//    assertTrue(retrievedFeedback.isPresent());
//    assertEquals(feedback.getId(), retrievedFeedback.get().getId());
//  }
//
//  @Test
//  void getAllFeedback() {
//    when(feedbackRepository.findAll()).thenReturn(Collections.singletonList(feedback));
//
//    List<Feedback> feedbackList = feedbackService.getAllFeedback();
//
//    assertNotNull(feedbackList);
//    assertFalse(feedbackList.isEmpty());
//    assertEquals(1, feedbackList.size());
//    assertEquals(feedback.getId(), feedbackList.get(0).getId());
//  }
//
//  @Test
//  void updateFeedback() {
//    when(feedbackRepository.existsById(feedback.getId())).thenReturn(true);
//    when(feedbackRepository.save(feedback)).thenReturn(feedback);
//
//    Feedback updatedFeedback = feedbackService.updateFeedback(feedback);
//
//    assertNotNull(updatedFeedback);
//    assertEquals(feedback.getId(), updatedFeedback.getId());
//  }
//
//  @Test
//  void updateNonExistentFeedback() {
//    when(feedbackRepository.existsById(feedback.getId())).thenReturn(false);
//
//    assertThrows(IllegalStateException.class, () -> feedbackService.updateFeedback(feedback));
//  }
//
//  @Test
//  void getAverageRatingForClient() {
//    when(feedbackRepository.findByClientId(client.getId())).thenReturn(Arrays.asList(feedback));
//
//    double averageRating = feedbackService.getAverageRatingForClient(client.getId());
//
//    assertEquals(5, averageRating);
//  }
//}