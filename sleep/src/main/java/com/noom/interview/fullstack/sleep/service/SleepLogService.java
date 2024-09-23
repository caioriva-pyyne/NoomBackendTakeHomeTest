package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.model.dto.request.SleepLogCreateRequest;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;

import java.util.UUID;

/**
 * Interface that offers operations for sleep log.
 */
public interface SleepLogService {
    /**
     * Creates a sleep log.
     *
     * @param request data to create the sleep log
     * @param userId the id of the user to associate the sleep log
     * @return the created sleep log
     */
    SleepLog createSleepLog(SleepLogCreateRequest request, UUID userId);

    /**
     * Gets last night sleep log.
     *
     * @param user the id of the user associated to the log
     * @return the last night sleep log
     */
    SleepLog getLastNightSleepLog(UUID userId);
}
