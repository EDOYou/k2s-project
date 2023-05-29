package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.services.facade.AdminServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling administrative actions in the application.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminServiceFacade adminServiceFacade;

    /**
     * Constructs the controller with the AdminServiceFacade.
     *
     * @param adminServiceFacade the facade class handling admin services
     */
    @Autowired
    public AdminController(AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    /**
     * Handles the request for viewing the admin dashboard.
     *
     * @param page           The page number for the appointments list pagination
     * @param size           The number of appointments per page for the appointments list pagination
     * @param authentication The authentication object containing the authentication status and details of the authenticated user
     * @param model          The model object to add attributes to for the view
     * @return The view name for the admin dashboard
     */
    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "4") int size,
                                 Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        PageRequest pageReq = PageRequest.of(page, size, Sort.by("id"));
        Page<Appointment> appointments = adminServiceFacade.findAllAppointments(pageReq);
        model.addAttribute("appointments", appointments);
        return "admin/dashboard";
    }

    /**
     * Handles the request for changing the time slot of an appointment.
     *
     * @param appointmentId The ID of the appointment to change the time slot of
     * @param newTimeSlot   The new time slot for the appointment
     * @return The redirect view name for the admin dashboard
     */
    @PostMapping("/changeTimeSlot/{appointmentId}")
    public String changeTimeSlot(@PathVariable("appointmentId") Long appointmentId,
                                 @RequestParam("newTimeSlot") String newTimeSlot) {
        adminServiceFacade.changeTimeSlot(appointmentId, newTimeSlot);
        return "redirect:/admin/dashboard";
    }

    /**
     * Handles the request for accepting a payment for an appointment.
     *
     * @param appointmentId The ID of the appointment to accept payment for
     * @return The redirect view name for the admin dashboard
     */
    @PostMapping("/acceptPayment/{appointmentId}")
    public String acceptPayment(@PathVariable("appointmentId") Long appointmentId) {
        adminServiceFacade.updatePaymentStatus(appointmentId);
        return "redirect:/admin/dashboard";
    }

    /**
     * Handles the request for cancelling an appointment.
     *
     * @param appointmentId The ID of the appointment to cancel
     * @return The redirect view name for the admin dashboard
     */
    @PostMapping("/cancelAppointment/{appointmentId}")
    public String cancelAppointment(@PathVariable("appointmentId") Long appointmentId) {
        adminServiceFacade.cancelAppointment(appointmentId);
        return "redirect:/admin/dashboard";
    }

    /**
     * Handles the request for approving a hairdresser.
     *
     * @param hairdresserId The ID of the hairdresser to approve
     * @return The redirect view name for the hairdresser registration review
     */
    @PostMapping("/approveHairdresser/{userId}")
    public String approveHairdresser(@PathVariable("userId") Long hairdresserId) {
        adminServiceFacade.approveHairdresser(hairdresserId);
        return "redirect:/admin/hairdresserRegistrationReview";
    }

    /**
     * Handles the request for rejecting a hairdresser.
     *
     * @param hairdresserId The ID of the hairdresser to reject
     * @return The redirect view name for the hairdresser registration review
     */
    @PostMapping("/rejectHairdresser/{userId}")
    public String rejectHairdresser(@PathVariable("userId") Long hairdresserId) {
        adminServiceFacade.rejectHairdresser(hairdresserId);
        return "redirect:/admin/hairdresserRegistrationReview";
    }

    /**
     * Displays the page for reviewing hairdresser registrations.
     *
     * @param page  The page number for the hairdressers list pagination
     * @param size  The number of hairdressers per page for the hairdressers list pagination
     * @param model The model object to add attributes to for the view
     * @return The view name for the hairdresser registration review
     */
    @GetMapping("/hairdresserRegistrationReview")
    public String reviewHairdresserRegistration(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "4") int size,
                                                Model model) {

        if (page < 0) {
            page = 0;
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Hairdresser> hairdressers = adminServiceFacade.findAllYetNotApproved(false, pageRequest);
        model.addAttribute("hairdressers", hairdressers);
        return "admin/hairdresserRegistrationReview";
    }

    /**
     * Displays the form for adding a new beauty service.
     *
     * @param model The model object to add attributes to for the view
     * @return The view name for the service addition form
     */
    @GetMapping("/add_service")
    public String showAddServiceForm(Model model) {
        model.addAttribute("service", new BeautyService());
        return "admin/add_service";
    }

    /**
     * Handles the request for adding a new beauty service.
     *
     * @param service The beauty service object to add
     * @return The redirect view name for the services list
     */
    @PostMapping("/add_service")
    public String handleAddService(@ModelAttribute("service") BeautyService service) {
        adminServiceFacade.saveService(service);
        return "redirect:/admin/services";
    }


    /**
     * Displays the list of beauty services.
     *
     * @param model The model object to add attributes to for the view
     * @return The view name for the services list
     */
    @GetMapping("/services")
    public String showServices(Model model) {
        List<BeautyService> services = adminServiceFacade.findAllBeautyServices();
        model.addAttribute("services", services);
        return "admin/services";
    }

    /**
     * Displays the form for assigning a beauty service to a hairdresser.
     *
     * @param authentication The authentication object containing the authentication status and details of the authenticated user
     * @param model          The model object to add attributes to for the view
     * @return The view name for the service assignment form
     */
    @GetMapping("/assign_service")
    public String showAssignServiceForm(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        List<BeautyService> services = adminServiceFacade.findAllBeautyServices();
        List<Hairdresser> hairdressers = adminServiceFacade.findAllHairdressers();
        model.addAttribute("services", services);
        model.addAttribute("hairdressers", hairdressers);
        return "admin/assign_service";
    }

    /**
     * Handles the request for assigning a beauty service to a hairdresser.
     *
     * @param hairdresserId The ID of the hairdresser to assign the service to
     * @param serviceId     The ID of the service to assign to the hairdresser
     * @return The redirect view name for the service assignment form
     */
    @PostMapping("/assign_service")
    public String handleAssignService(@ModelAttribute("hairdresserId") Long hairdresserId,
                                      @RequestParam("serviceId") Long serviceId) {
        adminServiceFacade.assignServiceToHairdresser(hairdresserId, serviceId);
        return "redirect:/admin/assign_service";
    }

    /**
     * Displays the list of hairdressers.
     *
     * @param page  The page number for the hairdressers list pagination
     * @param size  The number of hairdressers per page for the hairdressers list pagination
     * @param model The model object to add attributes to for the view
     * @return The view name for the hairdressers list
     */
    @GetMapping("/hairdressers")
    public String showHairdressers(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "4") int size, Model model) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Hairdresser> hairdressers = adminServiceFacade.findHairdressersWithServices(pageRequest);
        model.addAttribute("hairdressers", hairdressers);
        return "admin/hairdressers";
    }

}