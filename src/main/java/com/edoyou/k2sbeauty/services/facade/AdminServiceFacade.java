package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.Appointment;
import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.WorkingHours;
import com.edoyou.k2sbeauty.entities.payment.PaymentStatus;
import com.edoyou.k2sbeauty.exceptions.ResourceNotFoundException;
import com.edoyou.k2sbeauty.exceptions.RoleNotFoundException;
import com.edoyou.k2sbeauty.services.implementations.NotificationService;
import com.edoyou.k2sbeauty.services.interfaces.AppointmentService;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * The {@code AdminServiceFacade} class provides various administrative services
 * for managing appointments, hairdressers, beauty services and working hours.
 * <p>
 * The class provides functionalities such as finding all appointments, changing
 * appointment time slots, updating payment status, cancelling appointments, approving
 * or rejecting hairdressers, finding hairdressers based on their approval status,
 * saving beauty services, assigning services to hairdressers, deleting working hours,
 * and notifying hairdressers of changes to their status.
 * <p>
 * The class includes methods for managing individual entities of the system such as
 * appointments, hairdressers, and beauty services. These entities are manipulated based
 * on the business rules defined in the service layer.
 * <p>
 * Here is an example of how this class can be used:
 * <blockquote><pre>
 *     AdminServiceFacade adminServiceFacade = new AdminServiceFacade(appointmentService,
 *       hairdresserService, beautyServiceService, roleService, notificationService, workingHoursService, entityManager);
 *     adminServiceFacade.findAllAppointments(pageable);
 *     adminServiceFacade.changeTimeSlot(appointmentId, newTimeSlot);
 *     adminServiceFacade.updatePaymentStatus(appointmentId);
 *     adminServiceFacade.cancelAppointment(appointmentId);
 * </pre></blockquote>
 * <p>
 * The class {@code AdminServiceFacade} provides methods for dealing with different entities and
 * their corresponding data in the context of administrative tasks.
 * <p>
 * The Spring framework provides special support for the service layer through the use of
 * the {@code @Service} annotation. This annotation allows Spring to automatically manage
 * instances of this class and integrate them into its application context.
 * <p>
 * Unless otherwise noted, passing a {@code null} argument to a method in this class will
 * cause a {@link NullPointerException} to be thrown.
 *
 * @author Taghiyev Kanan
 * @see com.edoyou.k2sbeauty.services.interfaces.AppointmentService
 * @see com.edoyou.k2sbeauty.services.interfaces.HairdresserService
 * @see com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService
 * @see com.edoyou.k2sbeauty.services.interfaces.RoleService
 * @see com.edoyou.k2sbeauty.services.interfaces.WorkingHoursService
 * @see jakarta.persistence.EntityManager
 * @since 2023-05-28
 */
@Service
public class AdminServiceFacade {

    private static final Logger LOGGER = LogManager.getLogger(AdminServiceFacade.class.getName());
    private final AppointmentService appointmentService;
    private final HairdresserService hairdresserService;
    private final BeautyServiceService beautyServiceService;
    private final RoleService roleService;
    private final NotificationService notificationService;
    private final WorkingHoursService workingHoursService;
    private final EntityManager entityManager;

    /**
     * Constructor for AdminServiceFacade.
     * It injects the necessary services and the EntityManager through Dependency Injection.
     *
     * @param appointmentService   Provides functionalities related to appointments.
     * @param hairdresserService   Provides functionalities related to hairdressers.
     * @param beautyServiceService Provides functionalities related to beauty services.
     * @param roleService          Provides functionalities related to roles.
     * @param notificationService  Provides functionalities related to notifications.
     * @param workingHoursService  Provides functionalities related to working hours.
     * @param entityManager        Provides the capability to interact with the database.
     */
    @Autowired
    public AdminServiceFacade(AppointmentService appointmentService, HairdresserService hairdresserService, BeautyServiceService beautyServiceService, RoleService roleService, NotificationService notificationService, WorkingHoursService workingHoursService, EntityManager entityManager) {
        this.appointmentService = appointmentService;
        this.hairdresserService = hairdresserService;
        this.beautyServiceService = beautyServiceService;
        this.roleService = roleService;
        this.notificationService = notificationService;
        this.workingHoursService = workingHoursService;
        this.entityManager = entityManager;
    }

    /**
     * Fetches all appointments. By using the Pageable parameter,
     * this method supports pagination and sorting of the results.
     *
     * @param pageable Defines the pagination and sorting parameters.
     * @return A Page of appointments.
     */
    public Page<Appointment> findAllAppointments(Pageable pageable) {
        LOGGER.info("Find all appointments in Admin Facade ...");
        return this.appointmentService.findAllAppointments(pageable);
    }

    /**
     * Updates the timeslot for a given appointment. This method first parses
     * the new timeslot string into LocalDateTime object. It then fetches
     * the appointment and sets its timeslot to the new timeslot.
     *
     * @param appointmentId The ID of the appointment.
     * @param newTimeSlot   The new timeslot in ISO_LOCAL_DATE_TIME format.
     * @throws ResourceNotFoundException if the appointment does not exist.
     */
    public void changeTimeSlot(Long appointmentId, String newTimeSlot) {
        LOGGER.info("Change the appointment's timeslot ...");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime newAppointmentTime = LocalDateTime.parse(newTimeSlot, formatter);
        Appointment appointment = appointmentService.findById(appointmentId).orElseThrow(() -> new ResourceNotFoundException("Appointment not found for this id :: " + appointmentId));

        appointment.setAppointmentTime(newAppointmentTime);
        appointmentService.updateAppointment(appointmentId, appointment);
    }


    /**
     * Updates the payment status of a given appointment to 'PAID'.
     *
     * @param appointmentId The ID of the appointment.
     */
    public void updatePaymentStatus(Long appointmentId) {
        LOGGER.info("Updating the payment status of an appointment ...");
        PaymentStatus status = PaymentStatus.fromString("PAID", PaymentStatus.PENDING);
        appointmentService.updatePaymentStatus(appointmentId, status);
    }

    /**
     * Deletes an appointment. The method invokes the deleteAppointment method
     * of the AppointmentService, which takes care of deleting the appointment.
     *
     * @param appointmentId The ID of the appointment.
     */
    public void cancelAppointment(Long appointmentId) {
        LOGGER.info("Admin cancels the appointment ...");
        appointmentService.deleteAppointment(appointmentId, "ROLE_ADMIN");
    }

    /**
     * Approves a hairdresser. First, it fetches the hairdresser and then
     * adds the 'ROLE_HAIRDRESSER' role to the hairdresser. It also sets the approved flag to true.
     *
     * @param hairdresserId The ID of the hairdresser.
     * @throws ResourceNotFoundException if the hairdresser does not exist.
     * @throws RoleNotFoundException     if the 'ROLE_HAIRDRESSER' role does not exist.
     */
    @Transactional
    public void approveHairdresser(Long hairdresserId) {
        LOGGER.info("Admin employs the appointment ...");
        Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

        if (hairdresser == null) {
            throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
        }

        Role hairdresserRole = roleService.getRoleByName("ROLE_HAIRDRESSER").orElseThrow(() -> new RoleNotFoundException("Role not found."));

        hairdresser.getRoles().add(hairdresserRole);
        hairdresser.setApproved(true);
        hairdresserService.saveHairdresser(hairdresser);
        notifyHairdresser(hairdresser);
    }

    /**
     * Rejects a hairdresser application. This method first fetches the hairdresser,
     * then deletes all associated working hours and sends a rejection notification
     * to the hairdresser. Finally, it deletes the hairdresser. {@code entityManager.flush()}
     * forces Hibernate to execute the {@code SQL DELETE }statement for the hairdresser immediately,
     * ensuring that when deleteWorkingHours is called,
     * the working hours of the deleted hairdresser are indeed deletable.
     *
     * @param hairdresserId The ID of the hairdresser.
     * @throws ResourceNotFoundException if the hairdresser does not exist.
     */
    @Transactional
    public void rejectHairdresser(Long hairdresserId) {
        LOGGER.info("Admin rejects the application of an hairdresser ...");
        Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

        if (hairdresser == null) {
            throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
        }

        Set<WorkingHours> workingHoursSet = hairdresser.getWorkingHours();
        notifyHairdresser(hairdresser);
        hairdresserService.deleteHairdresser(hairdresserId);
        entityManager.flush();
        deleteWorkingHours(workingHoursSet);
    }

    /**
     * Fetches all hairdressers who are not approved yet. This method supports pagination and sorting of the results.
     *
     * @param isApproved The approval status to filter the hairdressers by.
     * @param pageable   Defines the pagination and sorting parameters.
     * @return A Page of unapproved hairdressers.
     */
    public Page<Hairdresser> findAllYetNotApproved(Boolean isApproved, Pageable pageable) {
        LOGGER.info("Display all the hairdressers whose is_approved column is false ...");
        return hairdresserService.findAllHairdressersByApprovalStatus(isApproved, pageable);
    }

    /**
     * Fetches all registered hairdressers.
     *
     * @return A list of all hairdressers.
     */
    public List<Hairdresser> findAllHairdressers() {
        return hairdresserService.findAllHairdressers();
    }

    /**
     * Saves a new beauty service. The beauty service object is passed to
     * the BeautyServiceService which takes care of persisting the object.
     *
     * @param beautyService The beauty service to be saved.
     */
    public void saveService(BeautyService beautyService) {
        LOGGER.info("Adding the beauty service ...");
        beautyServiceService.saveService(beautyService);
    }

    /**
     * Fetches all beauty services.
     *
     * @return A list of all beauty services.
     */
    public List<BeautyService> findAllBeautyServices() {
        return beautyServiceService.findAllServices();
    }

    /**
     * Assigns a service to a hairdresser. This method fetches the hairdresser
     * and the beauty service and then adds the beauty service to the hairdresser's
     * list of services. The updated hairdresser is then saved.
     *
     * @param hairdresserId The ID of the hairdresser.
     * @param serviceId     The ID of the service.
     * @throws ResourceNotFoundException if the hairdresser or the service does not exist.
     */
    @Transactional
    public void assignServiceToHairdresser(Long hairdresserId, Long serviceId) {
        LOGGER.info("Assigning the service to the hairdresser ...");
        Hairdresser hairdresser = hairdresserService.findById(hairdresserId);
        if (hairdresser == null) {
            throw new ResourceNotFoundException("Hairdresser not found for this id :: " + hairdresserId);
        }
        BeautyService beautyService = beautyServiceService.findById(serviceId).orElseThrow();
        hairdresser.getBeautyServices().add(beautyService);
        hairdresserService.saveHairdresser(hairdresser);
    }

    /**
     * Fetches all hairdressers who have been assigned services.
     * This method supports pagination and sorting of the results.
     *
     * @param pageable Defines the pagination and sorting parameters.
     * @return A Page of hairdressers with services.
     */
    public Page<Hairdresser> findHairdressersWithServices(Pageable pageable) {
        return hairdresserService.findAllWithBeautyServices(pageable);
    }

    /**
     * Deletes the working hours from the system. This is typically done when
     * a hairdresser is rejected and all their working hours are removed.
     *
     * @param workingHoursSet The set of working hours to be deleted.
     */
    public void deleteWorkingHours(Set<WorkingHours> workingHoursSet) {
        LOGGER.info("Deleting the working hours when hairdresser is rejected ...");
        for (WorkingHours workingHours : workingHoursSet) {
            entityManager.refresh(workingHours);
            if (workingHours.getHairdressers().isEmpty()) {
                workingHoursService.deleteWorkingHours(workingHours.getId());
            }
        }
    }

                    /**
     * Sends a notification to the hairdresser. The content of the notification
     * varies depending on whether the hairdresser was approved or not.
     *
     * @param hairdresser The hairdresser to be notified.
     */
    public void notifyHairdresser(Hairdresser hairdresser) {
        LOGGER.info("Mail sent to the hairdresser !");
        if (hairdresser.isApproved()) {
            String message = "Dear " + hairdresser.getFirstName() + ", your application has been approved. Welcome to our team!";
            notificationService.sendNotification(hairdresser.getEmail(), "Application Approved", message);
        } else {
            String message = "Dear " + hairdresser.getFirstName() + ", your application has been reviewed and unfortunately, you were not accepted at this time. Please consider applying again in the future.";
            notificationService.sendNotification(hairdresser.getEmail(), "Application Rejected", message);
        }
    }

}