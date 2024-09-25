package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.exception.BadRequestException;
import com.noom.interview.fullstack.sleep.model.dto.request.SleepLogCreateRequest;
import com.noom.interview.fullstack.sleep.model.dto.response.SleepLogLastDaysAverageResponse;
import com.noom.interview.fullstack.sleep.model.dto.response.SleepLogResponse;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.UUID;

// TODO Add tests
// TODO Update README

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
     * @param userId the user id
     * @return the created sleep log
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public SleepLogResponse createSleepLog(
            @RequestBody @Valid SleepLogCreateRequest request,
            @RequestParam @NotNull UUID userId
    ) {
        var start = request.getStartDateTimeInBed();
        var end = request.getEndDateTimeInBed();
        var startDatePlus2Days = start.toLocalDate().plusDays(2);
        if (!end.toLocalDate().isBefore(startDatePlus2Days)) {
            throw new BadRequestException("Difference in days between 'startDateTimeInBed' and 'endDateTimeInBed' must be zero or one");
        } else if (end.toLocalDate().isBefore(start.toLocalDate())) {
            throw new BadRequestException("'startDateTimeInBed' must be before 'endDateTimeInBed'");
        }

        SleepLog sleepLog = sleepLogService.upsertSleepLog(request, userId);

        // Even though the DTO is similar to the entity, using it helps with
        // separating the app's internal model from the API contract. Changing
        // the response wouldn't imply changing the entity.
        return createSleepLogResponse(sleepLog);
    }

    /**
     * Gets last night sleep log.
     *
     * @param userId the user id
     * @return the last night sleep log
     */
    @GetMapping("/last-night")
    @ResponseStatus(value = HttpStatus.OK)
    public SleepLogResponse getLastNightSleepLog(
            @RequestParam @NotNull UUID userId
    ) {
        SleepLog sleepLog = sleepLogService.getLastNightSleepLog(userId);
        return createSleepLogResponse(sleepLog);
    }

    /**
     * Gets average for last days of sleep logs.
     *
     * @param userId the user id
     * @param numOfDays the number of the days when calculating the average. Defaults to 30
     * @return the response for the average of the last days of sleep logs
     */
    @GetMapping("/last-days-average")
    @ResponseStatus(value = HttpStatus.OK)
    public SleepLogLastDaysAverageResponse getLastDaysAverageSleepLog(
            @RequestParam @NotNull UUID userId,
            @RequestParam(defaultValue = "30") @Min(30) @Max(3652) Integer numOfDays

    ) {
        return sleepLogService.getLastDaysAverage(userId, numOfDays);
    }

    private SleepLogResponse createSleepLogResponse(SleepLog sleepLog) {
        return new SleepLogResponse(
                sleepLog.getSleepDate(),
                sleepLog.getDateTimeInBedStart().toLocalTime(),
                sleepLog.getDateTimeInBedEnd().toLocalTime(),
                Duration.between(sleepLog.getDateTimeInBedStart(), sleepLog.getDateTimeInBedEnd()),
                sleepLog.getFeeling()
        );
    }
}
