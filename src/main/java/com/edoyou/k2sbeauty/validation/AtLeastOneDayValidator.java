package com.edoyou.k2sbeauty.validation;

import com.edoyou.k2sbeauty.annotation.AtLeastOneDay;
import com.edoyou.k2sbeauty.dto.WorkingHoursDTO;
import com.edoyou.k2sbeauty.entities.model.wrapper.WorkingHoursWrapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Custom validator for validating that there is at least one day with working hours.
 * <p>
 * This class implements ConstraintValidator, allowing it to be used to validate @AtLeastOneDay
 * annotations on WorkingHoursWrapper objects. The isValid method checks if the
 * WorkingHoursWrapper has at least one day with both start and end working hours.
 *
 * @author Taghiyev Kanan
 * @see jakarta.validation.ConstraintValidator
 * @since 2023-05-28
 */
public class AtLeastOneDayValidator implements
        ConstraintValidator<AtLeastOneDay, WorkingHoursWrapper> {

    private final static Logger LOGGER = Logger.getLogger(AtLeastOneDayValidator.class.getName());

    /**
     * Checks if the WorkingHoursWrapper object is valid according to the @AtLeastOneDay constraint.
     * <p>
     * A WorkingHoursWrapper is considered valid if at least one day (represented by the values in
     * the WorkingHoursDtoMap) has both start and end working hours.
     *
     * @param workingHoursWrapper the WorkingHoursWrapper object to check
     * @param context             context in which the constraint is evaluated
     * @return true if the WorkingHoursWrapper is valid, false otherwise
     */
    @Override
    public boolean isValid(WorkingHoursWrapper workingHoursWrapper,
                           ConstraintValidatorContext context) {
        Map<Integer, WorkingHoursDTO> workingHoursDtoMap = workingHoursWrapper.getWorkingHoursMap();

        LOGGER.info("WorkingHoursDtoMap: " + workingHoursDtoMap);

        if (workingHoursDtoMap == null) {
            return false;
        }
        return workingHoursDtoMap.values().stream().anyMatch(workingHoursDto ->
                workingHoursDto.getStart() != null && workingHoursDto.getEnd() != null);
    }
}