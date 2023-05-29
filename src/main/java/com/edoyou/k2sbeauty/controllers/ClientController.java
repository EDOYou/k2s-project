package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.Client;
import com.edoyou.k2sbeauty.entities.model.Feedback;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.facade.ClientServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * The {@code ClientController} class is responsible for managing incoming HTTP requests related to
 * clients in the K2SBeauty system.
 * <p>
 * This includes functionalities such as registration of a new client, booking appointments,
 * viewing existing appointments, providing feedback and login functions.
 * <p>
 * The main responsibilities of the {@code ClientController} class are:
 * 1. Managing the registration of new clients.
 * 2. Managing the booking of appointments.
 * 3. Managing the viewing of existing appointments.
 * 4. Managing the submission of feedback.
 * 5. Showing the login and registration page.
 * <p>
 * Most of the business logic is delegated to the {@code ClientServiceFacade} class.
 *
 * @author Taghiyev Kanan
 * @see com.edoyou.k2sbeauty.services.facade.ClientServiceFacade
 * @since 2023-05-28
 */
@Controller
public class ClientController {

    private final ClientServiceFacade clientServiceFacade;

    /**
     * Constructor for {@code ClientController} class.
     *
     * @param clientServiceFacade is the business logic delegation class for the client operations.
     * @see com.edoyou.k2sbeauty.services.facade.ClientServiceFacade
     */
    @Autowired
    public ClientController(ClientServiceFacade clientServiceFacade) {
        this.clientServiceFacade = clientServiceFacade;
    }

    /**
     * Processes client registration form data.
     *
     * @param client is a {@code Client} object containing the details of the client to be registered.
     * @return redirect to the client appointments page.
     */
    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute("client") Client client) {
        clientServiceFacade.processRegistrationForm(client);
        return "redirect:/client/appointments";
    }

    /**
     * Shows the booking page to the client with a list of hairdressers and services.
     *
     * @param model is a {@code Model} object used to pass attributes to the view.
     * @return the booking page view.
     */
    @GetMapping("/client/book")
    public String showBookingPage(Model model) {
        List<Hairdresser> hairdressers = clientServiceFacade.getHairdressersWithServices();
        List<String> services = clientServiceFacade.getDistinctServiceNames();
        model.addAttribute("hairdressers", hairdressers);
        model.addAttribute("services", services);
        return "client/book";
    }

    /**
     * Processes the booking of an appointment for the authenticated client.
     *
     * @param authentication is a {@code Authentication} object containing the details of the authenticated client.
     * @param hairdresserId  is the id of the hairdresser for the appointment.
     * @param serviceName    is the name of the service for the appointment.
     * @param dateTime       is the date and time of the appointment.
     * @return redirect to the client appointments page.
     */
    @PostMapping("/client/book")
    public String bookAppointment(Authentication authentication,
                                  @RequestParam("hairdresserId") Long hairdresserId,
                                  @RequestParam("serviceName") String serviceName,
                                  @RequestParam("dateTime") String dateTime) {

        if (dateTime.isEmpty()) {
            return "redirect:/client/book";
        }

        clientServiceFacade.bookAppointment(authentication, hairdresserId, serviceName, dateTime);
        return "redirect:/client/appointments";
    }

    /**
     * This endpoint is used to get available timeslots for a particular hairdresser.
     *
     * @param hairdresserId The ID of the hairdresser.
     * @return A list of available time slots for the provided hairdresser.
     */
    @GetMapping("/client/timeslots")
    public ResponseEntity<List<String>> getTimeSlots(
            @RequestParam("hairdresserId") Long hairdresserId) {
        List<String> timeSlotStrings = clientServiceFacade.getTimeSlots(hairdresserId);
        return ResponseEntity.ok(timeSlotStrings);
    }

    /**
     * This endpoint is used to fetch all appointments of a logged-in client.
     *
     * @param authentication Authentication object of the logged-in client.
     * @param model          Model object to bind data to the view.
     * @return The view of the client's appointments page.
     */
    @GetMapping("/client/appointments")
    public String viewAppointments(Authentication authentication, Model model) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = clientServiceFacade.getClientAppointments(authentication);
        model.addAttribute("appointments", appointments);
        return "client/appointments";
    }


    /**
     * This endpoint shows the feedback form for a particular appointment.
     *
     * @param appointmentId The ID of the appointment for which feedback is to be given.
     * @param model         Model object to bind data to the view.
     * @return The view of the feedback form.
     */
    @GetMapping("/client/feedback")
    public String showFeedbackForm(@RequestParam("appointmentId") Long appointmentId, Model model) {
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("appointmentId", appointmentId);
        return "client/feedback";
    }

    /**
     * This endpoint is used to submit feedback for a particular appointment.
     *
     * @param authentication Authentication object of the logged-in client.
     * @param feedback       The feedback object containing feedback details.
     * @param appointmentId  The ID of the appointment for which feedback is being submitted.
     * @return A redirect to the admin's appointments page.
     */
    @PostMapping("/client/feedback")
    public String submitFeedback(Authentication authentication,
                                 @ModelAttribute("feedback") Feedback feedback,
                                 @RequestParam("appointmentId") Long appointmentId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        clientServiceFacade.saveFeedback(authentication, appointmentId, feedback);
        return "redirect:/admin/appointments";
    }

    /**
     * This endpoint shows the registration form to the client.
     *
     * @param model Model object to bind data to the view.
     * @return The view of the registration form.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("client", new Client());
        return "register";
    }

    /**
     * This endpoint is used to show the login page to the client.
     *
     * @return The view of the login page.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}