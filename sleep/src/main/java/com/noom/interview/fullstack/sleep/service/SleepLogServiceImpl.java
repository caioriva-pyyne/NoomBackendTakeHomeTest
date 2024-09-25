package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.exception.ResourceNotFoundException;
import com.noom.interview.fullstack.sleep.model.dto.request.SleepLogCreateRequest;
import com.noom.interview.fullstack.sleep.model.dto.response.SleepLogLastDaysAverageResponse;
import com.noom.interview.fullstack.sleep.model.entity.AfterSleepFeeling;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;
import com.noom.interview.fullstack.sleep.model.entity.User;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SleepLogServiceImpl implements SleepLogService {
    private static final Integer TOTAL_SECONDS_OF_A_DAY = 86400;
    private final SleepLogRepository sleepLogRepository;
    private final UserService userService;
    private final TimeManagementService timeManagementService;

    @Autowired
    public SleepLogServiceImpl(
            SleepLogRepository sleepLogRepository,
            UserService userService,
            TimeManagementService timeManagementService)
    {
        this.sleepLogRepository = sleepLogRepository;
        this.userService = userService;
        this.timeManagementService = timeManagementService;
    }

    @Override
    public SleepLog upsertSleepLog(SleepLogCreateRequest request, UUID userId) {
        User user = userService.getUser(userId);
        LocalDateTime start = request.getStartDateTimeInBed();
        LocalDateTime end = request.getEndDateTimeInBed();
        LocalDate sleepDate = defineSleepDate(start, end);
        SleepLog sleepLog;

        // Logs with the same date will be replaced by the most recent one.
        // This may happen when a user changes their client timezone to a place where
        // a night they have already slept did not occur and sleep again.
        Optional<SleepLog> existentSleepLog = sleepLogRepository.findBySleepDateAndUser(sleepDate, user);

        if (existentSleepLog.isPresent()) {
            sleepLog = existentSleepLog.get();
            sleepLog.setSleepDate(sleepDate);
            sleepLog.setDateTimeInBedStart(start);
            sleepLog.setDateTimeInBedEnd(end);
            sleepLog.setFeeling(request.getFeeling());
        } else {
            sleepLog = SleepLog
                    .builder()
                    .sleepDate(sleepDate)
                    .dateTimeInBedStart(start)
                    .dateTimeInBedEnd(end)
                    .feeling(request.getFeeling())
                    .user(user)
                    .build();
        }

        return sleepLogRepository.save(sleepLog);
    }

    @Override
    public SleepLog getLastNightSleepLog(UUID userId) {
        User user = userService.getUser(userId);

        // Same a way a user can change their client timezone to a day before a slept day,
        // the user can change it to a day after a day without sleep. In this case, a NotFound
        // response will be returned as it's expected that the day won't have a sleep log.
        SleepLog sleepLog = sleepLogRepository
                .findBySleepDateAndUser(timeManagementService.getCurrentDay().minusDays(1), user)
                .orElseThrow(() -> new ResourceNotFoundException("No last night sleep log found for user with id " + user.getId()));
        sleepLog.setSleepDate(sleepLog.getSleepDate());

        return sleepLog;
    }

    // It's also possible to create a native query and execute most part of the logic in the DB.
    // Decided to make in the Java code for the sake of the test, though.
    @Override
    public SleepLogLastDaysAverageResponse getLastDaysAverage(UUID userId, Integer numOfDays) {
        LocalDate endDate = timeManagementService.getCurrentDay().minusDays(1);
        LocalDate startDate = endDate.minusDays(numOfDays);
        var totalTimeInBed = 0L;
        var startAverageAfterMidnight = 0L;
        var countStartAfterMidnight = 0;
        var countStartBeforeMidnight = 0;
        var startAverageBeforeMidnight = 0L;
        var totalTimeInBedEndInSeconds = 0L;
        var feelingFrequencies = new HashMap<AfterSleepFeeling, Integer>();
        feelingFrequencies.put(AfterSleepFeeling.OK, 0);
        feelingFrequencies.put(AfterSleepFeeling.BAD, 0);
        feelingFrequencies.put(AfterSleepFeeling.GOOD, 0);

        User user = userService.getUser(userId);
        List<SleepLog> sleepLogs = sleepLogRepository.findAllByUserAndSleepDateBetween(user, startDate, endDate);

        if (sleepLogs.isEmpty()) {
            throw new ResourceNotFoundException("No sleep logs found for the last " + numOfDays + " days for user with id " + user.getId());
        }

        for (var sleepLog : sleepLogs) {
            LocalDateTime start = sleepLog.getDateTimeInBedStart();
            LocalDateTime end = sleepLog.getDateTimeInBedEnd();

            if (!start.isBefore(end.toLocalDate().atStartOfDay())) {
                countStartAfterMidnight++;
                startAverageAfterMidnight += start.toLocalTime().toSecondOfDay();
            } else {
                countStartBeforeMidnight++;
                startAverageBeforeMidnight += start.toLocalTime().toSecondOfDay();
            }

            totalTimeInBed += Duration.between(start, end).toSeconds();
            totalTimeInBedEndInSeconds += end.toLocalTime().toSecondOfDay();
            feelingFrequencies.put(
                    sleepLog.getFeeling(),
                    feelingFrequencies.get(sleepLog.getFeeling()) + 1
            );
        }

        // This is necessary because start date can be after midnight. Calculating a regular
        // average for all times without considering that will cause the average to be incorrect
        var totalTimeInBedStartInSeconds = (startAverageAfterMidnight / (countStartAfterMidnight == 0 ? 1 : countStartAfterMidnight))
                + (startAverageBeforeMidnight / (countStartBeforeMidnight == 0 ? 1 : countStartBeforeMidnight));

        // If the total of seconds is greater than the seconds of a day, it means the average is after
        // midnight
        if(totalTimeInBedStartInSeconds > TOTAL_SECONDS_OF_A_DAY) {
            totalTimeInBedStartInSeconds = totalTimeInBedStartInSeconds - TOTAL_SECONDS_OF_A_DAY;
        }

        var count = sleepLogs.size();
        return SleepLogLastDaysAverageResponse
                .builder()
                .startDate(startDate)
                .endDate(endDate)
                .averageTotalTimeInBed(Duration.ofSeconds(totalTimeInBed / count))
                .averageTimeInBedStart(LocalTime.ofSecondOfDay(totalTimeInBedStartInSeconds))
                .averageTimeInBedEnd(LocalTime.ofSecondOfDay(totalTimeInBedEndInSeconds / count))
                .feelingFrequencies(feelingFrequencies)
                .build();
    }

    private LocalDate defineSleepDate(LocalDateTime sleepStart, LocalDateTime sleepEnd) {
        LocalDate sleepDate = sleepStart.toLocalDate();

        if(sleepStart.toLocalDate().equals(sleepEnd.toLocalDate()))  {
            if(!sleepStart.toLocalTime().isBefore(LocalTime.MIDNIGHT)) {
                // In case the user started sleeping after midnight we still consider
                // the previous day as the sleep date
                sleepDate = sleepStart.toLocalDate().minusDays(1);
            }
        }

        return sleepDate;
    }
}
