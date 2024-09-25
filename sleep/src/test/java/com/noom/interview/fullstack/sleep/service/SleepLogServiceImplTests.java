package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.exception.ResourceNotFoundException;
import com.noom.interview.fullstack.sleep.model.dto.request.SleepLogCreateRequest;
import com.noom.interview.fullstack.sleep.model.dto.response.SleepLogLastDaysAverageResponse;
import com.noom.interview.fullstack.sleep.model.entity.AfterSleepFeeling;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;
import com.noom.interview.fullstack.sleep.model.entity.User;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// In a real case scenario, tests UserService should also be added. They were not added due to
// time constraint.
public class SleepLogServiceImplTests {
    @InjectMocks
    private SleepLogServiceImpl sleepLogService;

    @Mock
    private SleepLogRepository sleepLogRepository;

    @Mock
    private UserService userService;

    @Mock
    private TimeManagementService timeManagementService;

    private UUID userId;
    private User user;
    private SleepLogCreateRequest sleepLogCreateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User(); // Assume User class has a no-arg constructor
        user.setId(userId);

        // Initialize a sample SleepLogCreateRequest
        sleepLogCreateRequest = new SleepLogCreateRequest();
        sleepLogCreateRequest.setStartDateTimeInBed(LocalDateTime.of(2024, 9, 21, 22, 0));
        sleepLogCreateRequest.setEndDateTimeInBed(LocalDateTime.of(2024, 9, 22, 6, 0));
        sleepLogCreateRequest.setFeeling(AfterSleepFeeling.GOOD.toString());
        when(timeManagementService.getCurrentDay()).thenReturn(LocalDate.of(2024, 9, 24));
    }

    @Test
    public void upsertSleepLog_createsNewLog_whenNoExistingLog() {
        // Arrange
        var sleepLog = SleepLog.builder()
                .sleepDate(sleepLogCreateRequest.getStartDateTimeInBed().toLocalDate())
                .dateTimeInBedStart(sleepLogCreateRequest.getStartDateTimeInBed())
                .dateTimeInBedEnd(sleepLogCreateRequest.getEndDateTimeInBed())
                .feeling(sleepLogCreateRequest.getFeeling())
                .user(user)
                .build();

        when(userService.getUser(userId)).thenReturn(user);
        when(sleepLogRepository.findBySleepDateAndUser(any(), any())).thenReturn(Optional.empty());
        when(sleepLogRepository.save(any(SleepLog.class))).thenAnswer(invocation ->  sleepLog);

        // Act
        SleepLog result = sleepLogService.upsertSleepLog(sleepLogCreateRequest, userId);

        // Assert
        assertNotNull(result);
        ArgumentCaptor<SleepLog> sleepLogCaptor = ArgumentCaptor.forClass(SleepLog.class);
        verify(sleepLogRepository, times(1)).save(sleepLogCaptor.capture());

        SleepLog capturedSleepLog = sleepLogCaptor.getValue();
        assertEquals(sleepLogCreateRequest.getStartDateTimeInBed().toLocalDate(), capturedSleepLog.getSleepDate());
        assertEquals(sleepLogCreateRequest.getStartDateTimeInBed(), capturedSleepLog.getDateTimeInBedStart());
        assertEquals(sleepLogCreateRequest.getEndDateTimeInBed(), capturedSleepLog.getDateTimeInBedEnd());
        assertEquals(sleepLogCreateRequest.getFeeling(), capturedSleepLog.getFeeling());
        assertEquals(user, capturedSleepLog.getUser());
    }

    @Test
    public void upsertSleepLog_updatesExistentLog_whenLogExists() {
        // Arrange
        var sleepLog = SleepLog.builder()
                .id(UUID.randomUUID())
                .sleepDate(sleepLogCreateRequest.getStartDateTimeInBed().toLocalDate())
                .dateTimeInBedStart(sleepLogCreateRequest.getStartDateTimeInBed())
                .dateTimeInBedEnd(sleepLogCreateRequest.getEndDateTimeInBed())
                .feeling(sleepLogCreateRequest.getFeeling())
                .user(user)
                .build();

        when(userService.getUser(userId)).thenReturn(user);
        when(sleepLogRepository.findBySleepDateAndUser(any(), any())).thenReturn(Optional.of(sleepLog));
        when(sleepLogRepository.save(any(SleepLog.class))).thenAnswer(invocation ->  sleepLog);

        // Act
        SleepLog result = sleepLogService.upsertSleepLog(sleepLogCreateRequest, userId);

        // Assert
        assertNotNull(result);
        ArgumentCaptor<SleepLog> sleepLogCaptor = ArgumentCaptor.forClass(SleepLog.class);
        verify(sleepLogRepository, times(1)).save(sleepLogCaptor.capture());

        SleepLog capturedSleepLog = sleepLogCaptor.getValue();
        assertEquals(sleepLog.getId(), capturedSleepLog.getId());
        assertEquals(sleepLogCreateRequest.getStartDateTimeInBed().toLocalDate(), capturedSleepLog.getSleepDate());
        assertEquals(sleepLogCreateRequest.getStartDateTimeInBed(), capturedSleepLog.getDateTimeInBedStart());
        assertEquals(sleepLogCreateRequest.getEndDateTimeInBed(), capturedSleepLog.getDateTimeInBedEnd());
        assertEquals(sleepLogCreateRequest.getFeeling(), capturedSleepLog.getFeeling());
        assertEquals(user, capturedSleepLog.getUser());
    }

    @Test
    public void upsertSleepLog_createsNewLogWithPreviousDayForSleepDate_whenSleepStartedAfterMidnight() {
        // Arrange
        var sleepLog = SleepLog.builder()
                .sleepDate(sleepLogCreateRequest.getStartDateTimeInBed().toLocalDate())
                .dateTimeInBedStart(sleepLogCreateRequest.getStartDateTimeInBed().plusDays(1).withHour(0))
                .dateTimeInBedEnd(sleepLogCreateRequest.getEndDateTimeInBed())
                .feeling(sleepLogCreateRequest.getFeeling())
                .user(user)
                .build();

        when(userService.getUser(userId)).thenReturn(user);
        when(sleepLogRepository.findBySleepDateAndUser(any(), any())).thenReturn(Optional.empty());
        when(sleepLogRepository.save(any(SleepLog.class))).thenAnswer(invocation ->  sleepLog);

        // Act
        SleepLog result = sleepLogService.upsertSleepLog(sleepLogCreateRequest, userId);

        // Assert
        assertNotNull(result);
        ArgumentCaptor<SleepLog> sleepLogCaptor = ArgumentCaptor.forClass(SleepLog.class);
        verify(sleepLogRepository, times(1)).save(sleepLogCaptor.capture());

        SleepLog capturedSleepLog = sleepLogCaptor.getValue();
        assertEquals(sleepLog.getSleepDate(), capturedSleepLog.getSleepDate());
        assertEquals(sleepLogCreateRequest.getStartDateTimeInBed(), capturedSleepLog.getDateTimeInBedStart());
        assertEquals(sleepLogCreateRequest.getEndDateTimeInBed(), capturedSleepLog.getDateTimeInBedEnd());
        assertEquals(sleepLogCreateRequest.getFeeling(), capturedSleepLog.getFeeling());
        assertEquals(user, capturedSleepLog.getUser());
    }

    @Test
    public void getLastNightSleepLog_throwsNotFound_whenNoSleepLogExists() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(user);
        when(sleepLogRepository.findBySleepDateAndUser(LocalDate.now().minusDays(1), user))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            sleepLogService.getLastNightSleepLog(userId);
        });
    }

    @Test
    public void getLastDaysAverage_throwsNotFound_whenNoSleepLogsExistForPeriod() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(user);
        when(sleepLogRepository.findAllByUserAndSleepDateBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            sleepLogService.getLastDaysAverage(userId, 30);
        });
    }

    @Test
    public void getLastDaysAverage_returnsAverage_whenSleepLogsExistForPeriod() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(user);
        SleepLog log1 = createSleepLog(
                LocalDate.of(2024, 9, 22),
                LocalDateTime.of(2024, 9, 22, 22, 00),
                LocalDateTime.of(2024, 9, 23, 6, 00),
                AfterSleepFeeling.GOOD);
        SleepLog log2 = createSleepLog(
                LocalDate.of(2024, 9, 21),
                LocalDateTime.of(2024, 9, 22, 01, 00),
                LocalDateTime.of(2024, 9, 22, 5, 00),
                AfterSleepFeeling.BAD);
        SleepLog log3 = createSleepLog(
                LocalDate.of(2024, 9, 20),
                LocalDateTime.of(2024, 9, 20, 22, 00),
                LocalDateTime.of(2024, 9, 21, 8, 00),
                AfterSleepFeeling.OK);
        List<SleepLog> sleepLogs = List.of(log1, log2, log3);

        when(sleepLogRepository.findAllByUserAndSleepDateBetween(any(), any(), any())).thenReturn(sleepLogs);

        // Act
        SleepLogLastDaysAverageResponse averageResponse = sleepLogService.getLastDaysAverage(userId, 30);

        // Assert
        assertNotNull(averageResponse);
        assertEquals(LocalDate.of(2024, 8, 24), averageResponse.getStartDate());
        assertEquals(LocalDate.of(2024, 9, 23), averageResponse.getEndDate());
        assertEquals(Duration.ofHours(7).plusMinutes(20), averageResponse.getAverageTotalTimeInBed());
        assertEquals(LocalTime.of(23, 0), averageResponse.getAverageTimeInBedStart());
        assertEquals(LocalTime.of(06, 20), averageResponse.getAverageTimeInBedEnd());
        var feelingFrequencies = averageResponse.getFeelingFrequencies();
        assertEquals(1, feelingFrequencies.get(AfterSleepFeeling.BAD));
        assertEquals(1, feelingFrequencies.get(AfterSleepFeeling.OK));
        assertEquals(1, feelingFrequencies.get(AfterSleepFeeling.GOOD));
    }

    private SleepLog createSleepLog(LocalDate date, LocalDateTime start, LocalDateTime end, AfterSleepFeeling feeling) {
        SleepLog sleepLog = new SleepLog();
        sleepLog.setSleepDate(date);
        sleepLog.setDateTimeInBedStart(start);
        sleepLog.setDateTimeInBedEnd(end);
        sleepLog.setFeeling(feeling);

        return sleepLog;
    }
}
