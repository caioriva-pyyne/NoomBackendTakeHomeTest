package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.model.dto.request.SleepLogCreateRequest;
import com.noom.interview.fullstack.sleep.model.dto.response.SleepLogLastDaysAverageResponse;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;

import java.util.UUID;

/**
 * Interface that offers operations for sleep log.
 */
public interface SleepLogService {
    /**
     * Creates a sleep log or updates one if log for the same date already exists for the specified user.
     *
     * @param request data to create or update the sleep log
     * @param userId the id of the user to associate the sleep log
     * @return the created or updated sleep log
     */
    SleepLog upsertSleepLog(SleepLogCreateRequest request, UUID userId);

    /**
     * Gets last night sleep log.
     *
     * @param userId the id of the user associated to the log
     * @return the last night sleep log
     */
    SleepLog getLastNightSleepLog(UUID userId);

    /**
     * Gets average for last days of sleep logs.
     *
     * @param userId the user id
     * @param numOfDays the number of the days when calculating the average
     */
    SleepLogLastDaysAverageResponse getLastDaysAverage(UUID userId, Integer numOfDays);
}
