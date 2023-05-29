package com.edoyou.k2sbeauty.pojo;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.entities.model.Hairdresser;

import java.util.List;

import org.springframework.data.util.Pair;

/**
 * This class is a Plain Old Java Object (POJO) that is used to transfer data related to services and hairdressers.
 * It contains a list of pairs of services and hairdressers, as well as separate lists of services and hairdressers.
 * This object also holds sorting information and the selected hairdresser and service IDs.
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
public class ServicesData {

    /**
     * A list of pairs, where each pair contains a service and the hairdresser providing that service.
     */
    private final List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs;

    /**
     * A list of beauty services.
     */
    private List<BeautyService> services;

    /**
     * A list of hairdressers.
     */
    private List<Hairdresser> hairdressers;

    /**
     * The field name used for sorting the list of hairdressers.
     */
    private final String sortBy;

    /**
     * The ID of the selected hairdresser.
     */
    private final Long selectedHairdresser;

    /**
     * The ID of the selected service.
     */
    private final Long selectedService;

    /**
     * Constructs a new ServicesData object.
     *
     * @param serviceHairdresserPairs The list of service-hairdresser pairs
     * @param services                The list of services
     * @param hairdressers            The list of hairdressers
     * @param sortBy                  The field name used for sorting
     * @param selectedHairdresser     The ID of the selected hairdresser
     * @param selectedService         The ID of the selected service
     */
    public ServicesData(List<Pair<BeautyService, Hairdresser>> serviceHairdresserPairs,
                        List<BeautyService> services, List<Hairdresser> hairdressers, String sortBy,
                        Long selectedHairdresser, Long selectedService) {
        this.serviceHairdresserPairs = serviceHairdresserPairs;
        this.services = services;
        this.hairdressers = hairdressers;
        this.sortBy = sortBy;
        this.selectedHairdresser = selectedHairdresser;
        this.selectedService = selectedService;
    }

    public List<Pair<BeautyService, Hairdresser>> getServiceHairdresserPairs() {
        return serviceHairdresserPairs;
    }

    public List<BeautyService> getServices() {
        return services;
    }

    public void setServices(List<BeautyService> services) {
        this.services = services;
    }

    public List<Hairdresser> getHairdressers() {
        return hairdressers;
    }

    public void setHairdressers(List<Hairdresser> hairdressers) {
        this.hairdressers = hairdressers;
    }

    public String getSortBy() {
        return sortBy;
    }

    public Long getSelectedHairdresser() {
        return selectedHairdresser;
    }

    public Long getSelectedService() {
        return selectedService;
    }

}