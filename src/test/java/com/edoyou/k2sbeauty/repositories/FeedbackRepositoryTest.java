package com.edoyou.k2sbeauty.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FeedbackRepositoryTest {

  @Autowired
  private FeedbackRepository feedbackRepository;
  private Feedback feedback;

  @BeforeEach
  void setUp() {
    Client client = new Client();
    client.setFirstName("Barbara");
    client.setLastName("O'Conner");
    client.setEmail("myemail@gmail.com");
    client.setPassword("password");
    client.setPhone("36707733373");
    client.setCreatedAt(LocalDateTime.now());
    client.setUpdatedAt(LocalDateTime.now());

    feedback = new Feedback();
    feedback.setClient(client);
    feedback.setComment("Excellent!");
    feedback.setCreatedAt(LocalDateTime.now());
  }

  @Test
  public void shouldSaveFeedback() {
    Feedback savedFeedback = feedbackRepository.save(feedback);
    assertThat(savedFeedback).isNotNull();
    assertThat(savedFeedback.getId()).isNotNull();
  }

  @Test
  public void shouldFindByClientID() {
    feedbackRepository.save(feedback);

    List<Feedback> foundFeedbacks = feedbackRepository.findByClientId(feedback.getClient().getId());
    assertThat(foundFeedbacks).isNotNull();
    assertThat(foundFeedbacks.get(0)).isEqualTo(feedback);
  }

}
