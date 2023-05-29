package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.BeautyService;
import com.edoyou.k2sbeauty.repositories.BeautyServiceRepository;
import com.edoyou.k2sbeauty.services.interfaces.BeautyServiceService;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The {@code BeautyServiceServiceImpl} class provides an implementation of the {@code BeautyServiceService} interface.
 * It is responsible for the manipulation and business logic tied to the {@code BeautyService} entities.
 *
 * <p> This service implementation provides methods for saving a beauty service, fetching a beauty service by its ID,
 * fetching all beauty services, finding distinct service names, finding the first beauty service by its name and finding
 * all beauty services by their IDs.
 *
 * <p> The class uses Spring's {@code @Service} annotation to indicate it's a service provider
 * and should be automatically detected by Spring for dependency injection.
 *
 * <p> An instance of {@code BeautyServiceRepository} is autowired to facilitate operations with the database.
 *
 * <p> Example usage:
 * <pre>{@code
 * @Autowired
 * BeautyServiceService beautyServiceService;
 *
 * public void createService() {
 *   BeautyService newService = new BeautyService();
 *   newService.setName("Haircut");
 *   beautyServiceService.saveService(newService);
 * }
 * }</pre>
 *
 * @since 2023-05-28
 * @author Taghiyev Kanan
 * @see BeautyService
 * @see BeautyServiceService
 * @see BeautyServiceRepository
 */
@Service
public class BeautyServiceServiceImpl implements BeautyServiceService {

    private static final Logger LOGGER = LogManager.getLogger(
            BeautyServiceServiceImpl.class.getName());

    private final BeautyServiceRepository beautyServiceRepository;

    /**
     * Constructor for BeautyServiceServiceImpl.
     *
     * @param beautyServiceRepository The {@code BeautyServiceRepository} to interact with the underlying beauty service data.
     */
    @Autowired
    public BeautyServiceServiceImpl(BeautyServiceRepository beautyServiceRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Saves a beauty service instance. Before saving, checks if the service is not null.
     * If it is null, it throws a NullPointerException.
     *
     * @param service The {@code BeautyService} instance to save.
     * @throws NullPointerException if the service is null.
     */
    @Override
    public void saveService(BeautyService service) {
        LOGGER.info("Saving a beauty service...");
        if (service == null) {
            throw new NullPointerException("Service cannot be NULL");
        }
        beautyServiceRepository.save(service);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Finds a beauty service by its ID. If the ID is null, throws an IllegalArgumentException.
     *
     * @param id The ID of the beauty service.
     * @return An {@code Optional} containing the {@code BeautyService} instance if found, or an empty {@code Optional} if not found.
     * @throws IllegalArgumentException if the ID is null.
     */
    @Override
    public Optional<BeautyService> findById(Long id) {
        if (id == null) {
            LOGGER.error("Attempted to find service with null ID");
            throw new IllegalArgumentException("ID cannot be null");
        }
        LOGGER.info("Find service by its ID...");
        return beautyServiceRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Finds all beauty services that have approved hairdressers.
     *
     * @return A list of {@code BeautyService} instances with approved hairdressers.
     */
    @Override
    public List<BeautyService> findAll() {
        LOGGER.info("Find all services with approved hairdressers...");
        return beautyServiceRepository.findAllWithApprovedHairdressers();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Finds a page of beauty services according to the {@code Pageable} object provided.
     *
     * @param pageable The {@code Pageable} instance containing the pagination information.
     * @return A {@code Page} of {@code BeautyService} instances.
     */
    @Override
    public Page<BeautyService> findAll(Pageable pageable) {
        return beautyServiceRepository.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Finds all beauty services irrespective of the hairdressers' approval status.
     *
     * @return A list of all {@code BeautyService} instances.
     */
    @Override
    public List<BeautyService> findAllServices() {
        LOGGER.info("Find all services...");
        return beautyServiceRepository.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Finds distinct service names from all beauty services.
     *
     * @return A list of distinct service names.
     */
    @Override
    public List<String> findDistinctServiceNames() {
        LOGGER.info("Find distinct service names...");
        return beautyServiceRepository.findDistinctServiceNames();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Finds the first beauty service that matches the given name.
     *
     * @param name The name of the beauty service.
     * @return An {@code Optional} containing the {@code BeautyService} instance if found, or an empty {@code Optional} if not found.
     */
    @Override
    public Optional<BeautyService> findFirstByName(String name) {
        LOGGER.info("Find first service by name...");
        return beautyServiceRepository.findFirstByName(name);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Finds all beauty services by their IDs. If the ID list is null, it returns an empty list.
     * If an error occurs during the fetching process, it logs the error and throws a RuntimeException.
     *
     * @param ids The list of IDs of the beauty services.
     * @return A list of {@code BeautyService} instances corresponding to the IDs. If the ID list is null, it returns an empty list.
     * @throws RuntimeException if an error occurs during the fetching process.
     */
    @Override
    public List<BeautyService> findAllByIdIn(List<Long> ids) {
        if (ids == null) {
            LOGGER.info("No IDs provided. Returning an empty list.");
            return new ArrayList<>();
        }

        try {
            LOGGER.info("Fetching beauty services by their IDs.");
            return beautyServiceRepository.findAllById(ids);
        } catch (Exception e) {
            LOGGER.error("An error occurred while fetching beauty services by their IDs." + e);
            throw new RuntimeException(e);
        }
    }
}