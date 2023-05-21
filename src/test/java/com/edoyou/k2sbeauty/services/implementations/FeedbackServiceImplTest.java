package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.repositories.FeedbackRepository;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

  @Mock
  private FeedbackRepository feedbackRepository;

  @Mock
  private HairdresserService hairdresserService;

  @InjectMocks
  private FeedbackServiceImpl feedbackService;

  @Test
  public void createFeedback_test() {
    Feedback feedback = createSampleFeedback();
    Hairdresser hairdresser = feedback.getAppointment().getHairdresser();

    when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

    feedbackService.createFeedback(feedback);

    verify(feedbackRepository, times(1)).save(feedback);
    verify(hairdresserService, times(1)).updateRating(hairdresser);
  }

  @Test
  public void getFeedbackById_test() {
    Long id = 1L;
    Feedback feedback = createSampleFeedback();

    when(feedbackRepository.findById(id)).thenReturn(Optional.of(feedback));

    Optional<Feedback> foundFeedback = feedbackService.getFeedbackById(id);

    verify(feedbackRepository, times(1)).findById(id);
    assertEquals(feedback, foundFeedback.orElse(null));
  }

  private Feedback createSampleFeedback() {
    Feedback feedback = new Feedback();
    feedback.setId(1L);
    feedback.setComment("Great service!");
    feedback.setCreatedAt(LocalDateTime.now());
    feedback.setRating(5);

    Client client = new Client();
    feedback.setClient(client);

    Appointment appointment = createSampleAppointment();
    feedback.setAppointment(appointment);

    return feedback;
  }

  private Appointment createSampleAppointment() {
    Appointment appointment = new Appointment();
    appointment.setId(1L);
    appointment.setAppointmentTime(LocalDateTime.now().plusHours(1));
    appointment.setCompleted(false);
    appointment.setPaymentStatus(PaymentStatus.PAID);

    Hairdresser hairdresser = new Hairdresser();
    hairdresser.setSpecialization("Special");
    hairdresser.setRating(4.5);
    appointment.setHairdresser(hairdresser);

    BeautyService beautyService = new BeautyService();
    appointment.setBeautyService(beautyService);

    return appointment;
  }

}