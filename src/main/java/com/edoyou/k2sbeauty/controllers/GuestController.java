package com.edoyou.k2sbeauty.controllers;

import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.facade.GuestServiceFacade;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * GuestController is a Spring MVC Controller that handles all the HTTP requests related to guests.
 * It is the gateway between the client-side and the server-side for guest related operations.
 * The class is annotated with {@link org.springframework.stereotype.Controller} to indicate that it's a Spring MVC Controller.
 *
 * <p>
 * The class uses {@link GuestServiceFacade} to delegate the business logic related to guest operations.
 * All the endpoints exposed by the controller are prefixed with "/guest" as indicated by {@link org.springframework.web.bind.annotation.RequestMapping} annotation.
 * </p>
 *
 * @author Taghiyev Kanan
 * @since 2025-05-28
 */
@Controller
@RequestMapping("/guest")
public class GuestController {

    private final GuestServiceFacade guestServiceFacade;

    /**
     * This constructor initializes the {@link GuestServiceFacade} instance used by this controller.
     *
     * @param guestServiceFacade a {@link GuestServiceFacade} instance
     */
    public GuestController(
            GuestServiceFacade guestServiceFacade) {
        this.guestServiceFacade = guestServiceFacade;
    }

    /**
     * This method is a GET handler for "/services" endpoint.
     * It fetches the services data based on the provided parameters and adds them as model attributes to be used by the view.
     * The fetched data includes service and hairdresser pairs, services, hairdressers, sorting criteria, selected hairdresser and service.
     *
     * @param model         A {@link Model} object to which the attributes are added.
     * @param hairdresserId (Optional) The id of the hairdresser to filter the services.
     * @param serviceId     (Optional) The id of the service to filter the services.
     * @param sortBy        (Optional) The criteria to sort the services. The default value is "lastName".
     * @return The name of the view to render. In this case, "/guest/services".
     */
    @GetMapping("/services")
    public String viewServices(Model model,
                               @RequestParam(name = "hairdresser", required = false) Long hairdresserId,
                               @RequestParam(name = "service", required = false) Long serviceId,
                               @RequestParam(name = "sort", defaultValue = "lastName") String sortBy) {
        ServicesData servicesData = guestServiceFacade.getServicesData(hairdresserId, serviceId,
                sortBy);

        model.addAttribute("serviceHairdresserPairs", servicesData.getServiceHairdresserPairs());
        model.addAttribute("services", servicesData.getServices());
        model.addAttribute("hairdressers", servicesData.getHairdressers());
        model.addAttribute("sort", servicesData.getSortBy());
        model.addAttribute("selectedHairdresser", servicesData.getSelectedHairdresser());
        model.addAttribute("selectedService", servicesData.getSelectedService());
        return "/guest/services";
    }

}