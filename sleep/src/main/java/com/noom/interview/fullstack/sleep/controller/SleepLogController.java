package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.exception.BadRequestException;
import com.noom.interview.fullstack.sleep.model.dto.request.SleepLogCreateRequest;
import com.noom.interview.fullstack.sleep.model.dto.response.SleepLogResponse;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.Duration;
import java.util.UUID;

/**
 * Spring controller for sleep log related requests.
 */
@RestController
@RequestMapping("api/sleep")
public class SleepLogController {
    private static final String VALID_UUID_FORMAT = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
    private final SleepLogService sleepLogService;

    @Autowired
    public SleepLogController(SleepLogService sleepLogService) {
        this.sleepLogService = sleepLogService;
    }

    /**
     * Creates a sleep log.
     *
     * @param request data to create the sleep log
     * @return the created sleep log
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public SleepLogResponse createSleepLog(
            @RequestBody @Valid SleepLogCreateRequest request,
            @RequestParam
            @Pattern(regexp = VALID_UUID_FORMAT,
                    message = "'userId' should be in a valid UUID format") UUID userId
    ) {
        var start = request.getStartDateTimeInBed();
        var end = request.getEndDateTimeInBed();

        if (end.toLocalDate().isAfter(start.toLocalDate().plusDays(2)) ||
                end.toLocalDate().isBefore(start.toLocalDate())) {
            throw new BadRequestException("Difference in days between 'startDateTimeInBed' and 'endDateTimeInBed' must be zero or one");
        }

        SleepLog sleepLog = sleepLogService.createSleepLog(request, userId);

        // Even though the DTO is similar to the entity, using it helps with
        // separating the app's internal model from the API contract. Changing
        // the response wouldn't imply changing the entity.
        return createSleepLogResponse(sleepLog);
    }

    /**
     * Gets last night sleep log.
     *
     * @return the last night sleep log
     */
    @GetMapping("/last-night")
    @ResponseStatus(value = HttpStatus.OK)
    public SleepLogResponse getLastNightSleepLog(
            @RequestParam
            @Pattern(regexp = VALID_UUID_FORMAT,
                    message = "'userId' should be in a valid UUID format") UUID userId
    ) {
        SleepLog sleepLog = sleepLogService.getLastNightSleepLog(userId);
        return createSleepLogResponse(sleepLog);
    }

    private SleepLogResponse createSleepLogResponse(SleepLog sleepLog) {
        return new SleepLogResponse(
                sleepLog.getSleepDate(),
                sleepLog.getTimeInBedStart(),
                sleepLog.getTimeInBedEnd(),
                Duration.ofSeconds(sleepLog.getTotalTimeInBedInSeconds()),
                sleepLog.getFeeling()
        );
    }
}
