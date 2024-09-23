package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.exception.ResourceNotFoundException;
import com.noom.interview.fullstack.sleep.model.dto.request.SleepLogCreateRequest;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;
import com.noom.interview.fullstack.sleep.model.entity.User;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SleepLogServiceImpl implements SleepLogService {
    private final SleepLogRepository sleepLogRepository;
    private final UserService userService;

    @Autowired
    public SleepLogServiceImpl(SleepLogRepository sleepLogRepository, UserService userService) {
        this.sleepLogRepository = sleepLogRepository;
        this.userService = userService;
    }

    @Override
    public SleepLog createSleepLog(SleepLogCreateRequest request, UUID userId) {
        User user = userService.getUser(userId);
        LocalDateTime start = request.getStartDateTimeInBed();
        LocalDateTime end = request.getEndDateTimeInBed();
        LocalDate sleepDate = defineSleepDate(start, end);
        Long totalSleepDurationInSeconds = Duration.between(start, end).toSeconds();
        SleepLog sleepLog;

        // Logs with the same date will be replaced by the most recent one.
        // This may happen when a user changes their client timezone to a place where
        // a night they have already slept did not occur and sleep again.
        Optional<SleepLog> existentSleepLog = sleepLogRepository.findBySleepDateAndUser(sleepDate, user);

        if (existentSleepLog.isPresent()) {
            sleepLog = existentSleepLog.get();
            sleepLog.setSleepDate(sleepDate);
            sleepLog.setTimeInBedStart(start.toLocalTime());
            sleepLog.setTimeInBedEnd(end.toLocalTime());
            sleepLog.setTotalTimeInBedInSeconds(totalSleepDurationInSeconds);
            sleepLog.setFeeling(request.getFeeling());
        } else {
            sleepLog = SleepLog
                    .builder()
                    .sleepDate(sleepDate)
                    .timeInBedStart(start.toLocalTime())
                    .timeInBedEnd(end.toLocalTime())
                    .totalTimeInBedInSeconds(totalSleepDurationInSeconds)
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
                .findBySleepDateAndUser(LocalDate.now().minusDays(1), user)
                .orElseThrow(() -> new ResourceNotFoundException("No last night sleep log found for user with id " + user.getId()));
        sleepLog.setSleepDate(sleepLog.getSleepDate());

        return sleepLog;
    }

    private LocalDate defineSleepDate(LocalDateTime sleepStart, LocalDateTime sleepEnd) {
        LocalDate sleepDate = sleepStart.toLocalDate();

        if(sleepStart.toLocalDate().equals(sleepEnd.toLocalDate()))  {
            if(!sleepStart.toLocalTime().isBefore(LocalTime.MIDNIGHT)) {
                sleepDate = sleepStart.toLocalDate().minusDays(1);
            }
        }

        return sleepDate;
    }
}
