package com.edoyou.k2sbeauty.services.interfaces;

import com.edoyou.k2sbeauty.entities.model.BeautyService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * {@code BeautyServiceService} is an interface providing specifications for business operations
 * applicable to {@link BeautyService} entities.
 */
public interface BeautyServiceService {

    /**
     * Saves a new beauty service to the repository.
     *
     * @param service The beauty service to save.
     */
    void saveService(BeautyService service);

    /**
     * Finds a beauty service by ID.
     *
     * @param id The ID of the beauty service to find.
     * @return An Optional containing the found beauty service, or an empty Optional if not found.
     */
    Optional<BeautyService> findById(Long id);

    /**
     * Finds all beauty services.
     *
     * @return A list of all beauty services.
     */
    List<BeautyService> findAll();

    /**
     * Retrieves a page of all beauty services.
     *
     * @param pageable The pagination information.
     * @return A Page object containing beauty services.
     */
    Page<BeautyService> findAll(Pageable pageable);

    /**
     * Retrieves a list of all beauty services.
     *
     * @return A list of all beauty services.
     */
    List<BeautyService> findAllServices();

    /**
     * Retrieves a list of distinct service names.
     *
     * @return A list of distinct service names.
     */
    List<String> findDistinctServiceNames();

    /**
     * Retrieves the first beauty service found with a given name.
     *
     * @param name The name of the beauty service to find.
     * @return An Optional containing the found beauty service, or an empty Optional if not found.
     */
    Optional<BeautyService> findFirstByName(String name);

    /**
     * Retrieves a list of beauty services by a list of IDs.
     *
     * @param ids The list of IDs.
     * @return A list of beauty services.
     */
    List<BeautyService> findAllByIdIn(List<Long> ids);
}