package com.edoyou.k2sbeauty.services.facade;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;
import com.edoyou.k2sbeauty.exceptions.BeautyServiceNotFoundException;
import com.edoyou.k2sbeauty.exceptions.HairdresserNotFoundException;
import com.edoyou.k2sbeauty.pojo.ServicesData;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;
import com.edoyou.k2sbeauty.services.interfaces.HairdresserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * GuestServiceFacade is a Spring Service class that acts as a facade for operations related to services.
 * This class encapsulates the business logic and interactions between different service classes to provide a simplified
 * and unified interface to the controllers. It also helps to keep the controllers thin and the code more maintainable.
 *
 * <p>
 * It uses instances of {@link BeautyServiceService} and {@link HairdresserService} to interact with the underlying database.
 * </p>
 *
 * @author Taghiyev Kanan
 * @since 2025-05-28
 */
@Service
public class GuestServiceFacade {

    private static final Logger LOGGER = LogManager.getLogger(GuestServiceFacade.class.getName());

    private final BeautyServiceService beautyServiceService;
    private final HairdresserService hairdresserService;

    @Autowired
    public GuestServiceFacade(BeautyServiceService beautyServiceService,
                              HairdresserService hairdresserService) {
        this.beautyServiceService = beautyServiceService;
        this.hairdresserService = hairdresserService;
    }

    /**
     * Retrieves the service data based on the provided parameters. If the hairdresserId is not null, it retrieves the services data
     * for the specific hairdresser. If the serviceId is not null, it retrieves the services data for the specific service.
     * If both parameters are null, it retrieves all services data.
     *
     * @param hairdresserId (Optional) The id of the hairdresser
     * @param serviceId     (Optional) The id of the service
     * @param sortBy        (Optional) The criteria to sort the services
     * @return A {@link ServicesData} object containing the retrieved services data
     * @throws HairdresserNotFoundException   if the hairdresserId is not null and no hairdresser is found with that id
     * @throws BeautyServiceNotFoundException if the serviceId is not null and no service is found with that id
     * @throws IllegalArgumentException       if the sortBy value is not one of "serviceName", "lastName" or "rating"
     */
    public ServicesData getServicesData(Long hairdresserId, Long serviceId, String sortBy) {
        LOGGER.info("Guest viewing the services ...");

        if (hairdresserId != null) {
            LOGGER.info("Getting services data for HAIRDRESSER ...");
            return getServicesDataForHairdresser(hairdresserId);
        } else if (serviceId != null) {
            LOGGER.info("Getting services data for SERVICE ...");
            return getServicesDataForService(serviceId, sortBy);
        } else {
            LOGGER.info("Getting services data for ALL ...");
            return getServicesDataForAll(sortBy);
        }
    }

    /**
     * Retrieves all available services and hairdressers data.
     * It creates pairs of each service with all its associated hairdressers and sorts them based on the provided criteria.
     *
     * @param sortBy The criteria to sort the services
     * @return A {@link ServicesData} object containing all services and hairdressers data, sorted by the provided criteria
     */
    private ServicesData getServicesDataForAll(String sortBy) {
        var services = beautyServiceService.findAll();
        var hairdressers = hairdresserService.findAllHairdressers(sortBy);

        List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs = new ArrayList<>();
        for (BeautyService service : services) {
            for (Hairdresser hairdresser : service.getHairdressers()) {
                if (hairdresser.isApproved()) {
                    serviceHairdresserPairs.add(Pair.of(service, hairdresser));
                }
            }
        }

        serviceHairdresserPairs.sort(getServiceComparator(sortBy));
        return new ServicesData(serviceHairdresserPairs, services, hairdressers, sortBy, null, null);
    }

    /**
     * Retrieves the services data for a specific hairdresser.
     * It creates pairs of the hairdresser with all its associated services.
     *
     * @param hairdresserId The id of the hairdresser
     * @return A {@link ServicesData} object containing all services associated with the specific hairdresser
     * @throws HairdresserNotFoundException if no hairdresser is found with the provided id
     */
    private ServicesData getServicesDataForHairdresser(Long hairdresserId) {
        Hairdresser hairdresser = hairdresserService.findById(hairdresserId);

        if (hairdresser == null) {
            throw new HairdresserNotFoundException("Hairdresser not found.");
        }

        Set<BeautyService> beautyServices = hairdresser.getBeautyServices();
        List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs = new ArrayList<>();
        beautyServices.forEach(service -> serviceHairdresserPairs.add(Pair.of(service, hairdresser)));

        return new ServicesData(serviceHairdresserPairs, new ArrayList<>(beautyServices),
                Collections.singletonList(hairdresser), null, hairdresserId, null);
    }

    /**
     * Retrieves the services data for a specific service.
     * It creates pairs of the service with all its associated hairdressers and sorts them based on the provided criteria.
     *
     * @param serviceId The id of the service
     * @param sortBy    The criteria to sort the hairdressers
     * @return A {@link ServicesData} object containing all hairdressers associated with the specific service, sorted by the provided criteria
     * @throws BeautyServiceNotFoundException if no service is found with the provided id
     */
    private ServicesData getServicesDataForService(Long serviceId, String sortBy) {
        BeautyService service = beautyServiceService.findById(serviceId)
                .orElseThrow(() -> new BeautyServiceNotFoundException("Service not found."));
        Set<Hairdresser> hairdressers = service.getHairdressers();

        List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs = new ArrayList<>();
        hairdressers.forEach(hairdresser -> serviceHairdresserPairs.add(Pair.of(service, hairdresser)));

        return new ServicesData(serviceHairdresserPairs, Collections.singletonList(service),
                new ArrayList<>(hairdressers), sortBy, null, serviceId);
    }

    /**
     * Returns a comparator to sort the service-hairdresser pairs based on the provided criteria.
     * The criteria can be one of "serviceName", "lastName", or "rating". The "serviceName" criteria sorts the pairs based on the service name,
     * the "lastName" criteria sorts the pairs based on the hairdresser's last name, and the "rating" criteria sorts the pairs based on the hairdresser's rating in descending order.
     *
     * @param sortBy The criteria to sort the service-hairdresser pairs
     * @return A {@link Comparator} to sort the service-hairdresser pairs based on the provided criteria
     * @throws IllegalArgumentException if the sortBy value is not one of "serviceName", "lastName" or "rating"
     */
    private Comparator<Pair<BeautyService, Hairdresser>> getServiceComparator(String sortBy) {
        if ("serviceName".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(pair -> pair.getFirst().getName());
        } else if ("lastName".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(pair -> pair.getSecond().getLastName());
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(
                            (Pair<BeautyService, Hairdresser> pair) -> pair.getSecond().getRating())
                    .reversed();
        } else {
            throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }
    }
}