package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noom.interview.fullstack.sleep.model.dto.response.SleepLogLastDaysAverageResponse;
import com.noom.interview.fullstack.sleep.model.entity.AfterSleepFeeling;
import com.noom.interview.fullstack.sleep.model.entity.SleepLog;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SleepLogController.class)
@AutoConfigureMockMvc
public class SleepLogControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private SleepLogService sleepLogService;

    private final UUID userId = UUID.randomUUID();
    private final SleepLog sleepLog = createSleepLog();
    private final SleepLogLastDaysAverageResponse averageResponse = createAverageSleepLogsResponse();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createSleepLog_withValidData_returns201() throws Exception {
        // Arrange
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("startDateTimeInBed", "2024-09-21T22:00:00");
        jsonNode.put("endDateTimeInBed", "2024-09-22T06:00:00");
        jsonNode.put("feeling", "GOOD");
        String requestBody = objectMapper.writeValueAsString(jsonNode);
        when(sleepLogService.upsertSleepLog(any(), any())).thenReturn(sleepLog);

        // Act and assert
        mvc.perform(post("/api/sleep")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("data.sleepDate").value("2024-09-21"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.timeInBedStart").value("22:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.timeInBedEnd").value("06:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalTimeInBed").value("PT8H"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.feeling").value("GOOD"));
    }

    @Test
    public void createSleepLog_withInvalidUUID_returns400() throws Exception {
        // Arrange
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("startDateTimeInBed", "2024-09-21T22:00:00");
        jsonNode.put("endDateTimeInBed", "2024-09-22T06:00:00");
        jsonNode.put("feeling", "GOOD");
        String requestBody = objectMapper.writeValueAsString(jsonNode);

        // Act and assert
        mvc.perform(post("/api/sleep")
                        .param("userId", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Invalid UUID string: invalid-uuid"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSleepLogRequests")
    public void createSleepLog_withInvalidData_returns400(
            LocalDateTime start,
            LocalDateTime end,
            String feeling,
            HttpStatus expectedStatus,
            String expectedMessage
    ) throws Exception {
        // Arrange
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("startDateTimeInBed", start.toString());
        jsonNode.put("endDateTimeInBed", end.toString());
        jsonNode.put("feeling", feeling);
        String requestBody = objectMapper.writeValueAsString(jsonNode);

        // Act and assert
        mvc.perform(post("/api/sleep")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(expectedMessage));
    }

    @Test
    public void getLastNightSleepLog_withValidUserId_returnsSleepLog() throws Exception {
        // Arrange
        when(sleepLogService.getLastNightSleepLog(any())).thenReturn(sleepLog);

        // Act and assert
        mvc.perform(get("/api/sleep/last-night")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("data.sleepDate").value("2024-09-21"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.timeInBedStart").value("22:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.timeInBedEnd").value("06:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalTimeInBed").value("PT8H"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.feeling").value("GOOD"));
    }

    @Test
    public void getLastNightSleepLog_withInvalidUserId_Returns400() throws Exception {
        // Act and assert
        mvc.perform(get("/api/sleep/last-days-average")
                        .param("userId", "invalid-uuid")
                        .param("numOfDays", "30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("Invalid UUID string: invalid-uuid"));
    }

    @Test
    public void getLastDaysAverageSleepLog_withValidUserId_returnsAverage() throws Exception {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        when(sleepLogService.getLastDaysAverage(any(), eq(30))).thenReturn(averageResponse);

        // Act and assert
        mvc.perform(get("/api/sleep/last-days-average")
                        .param("userId", userId.toString())
                        .param("numOfDays", "30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("data.startDate")
                        .value(averageResponse.getStartDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.endDate")
                        .value(averageResponse.getEndDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.averageTotalTimeInBed")
                        .value(averageResponse.getAverageTotalTimeInBed().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.averageTimeInBedStart")
                        .value(averageResponse.getAverageTimeInBedStart().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("data.averageTimeInBedEnd")
                        .value(averageResponse.getAverageTimeInBedEnd().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("data.feelingFrequencies").isNotEmpty());
    }

    @Test
    public void getLastDaysAverageSleepLog_withInvalidUserId_Returns400() throws Exception {
        // Act and assert
        mvc.perform(get("/api/sleep/last-days-average")
                        .param("userId", "invalid-uuid")
                        .param("numOfDays", "30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("Invalid UUID string: invalid-uuid"));
    }

    private SleepLog createSleepLog() {
        return SleepLog
                .builder()
                .id(UUID.randomUUID())
                .sleepDate(LocalDate.of(2024, 9, 21))
                .dateTimeInBedStart(LocalDateTime.of(2024, 9, 21, 22, 0))
                .dateTimeInBedEnd(LocalDateTime.of(2024, 9, 22, 6, 0))
                .feeling(AfterSleepFeeling.GOOD)
                .build();
    }

    private SleepLogLastDaysAverageResponse createAverageSleepLogsResponse() {
        LocalDate endDate = LocalDate.of(2024, 8, 23);
        LocalDate startDate = endDate.minusDays(30);

        return new SleepLogLastDaysAverageResponse(
                startDate,
                endDate,
                Duration.ofHours(7),
                LocalTime.of(22, 0, 0),
                LocalTime.of(6, 0, 0),
                Map.of(AfterSleepFeeling.GOOD, 15, AfterSleepFeeling.OK, 10, AfterSleepFeeling.BAD, 5)
        );
    }

    private static Stream<Arguments> provideInvalidSleepLogRequests() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.now().minusDays(2).withHour(20),
                        LocalDateTime.now().minusDays(1).withHour(6),
                        "INVALID_FEELING", HttpStatus.BAD_REQUEST,
                        "'feeling' must be one of the following: OK, BAD or GOOD"),
                Arguments.of(
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().minusDays(1).withHour(6),
                        "GOOD", HttpStatus.BAD_REQUEST,
                        "'startDateTimeInBed' can not be in the future"),
                Arguments.of(LocalDateTime.now().minusDays(2).withHour(20),
                        LocalDateTime.now().plusDays(1),
                        "GOOD", HttpStatus.BAD_REQUEST,
                        "'endDateTimeInBed' can not be in the future"),
                Arguments.of(LocalDateTime.now().minusDays(2).withHour(20),
                        LocalDateTime.now().minusDays(3).withHour(6),
                        "GOOD",
                        HttpStatus.BAD_REQUEST,
                        "'startDateTimeInBed' must be before 'endDateTimeInBed'"),
                Arguments.of(
                        LocalDateTime.now().minusDays(3).withHour(20),
                        LocalDateTime.now().minusDays(1).withHour(6),
                        "GOOD", HttpStatus.BAD_REQUEST,
                        "Difference in days between 'startDateTimeInBed' and 'endDateTimeInBed' must be zero or one"),
                Arguments.of(
                        LocalDateTime.now().minusDays(2).withHour(14),
                        LocalDateTime.now().minusDays(1).withHour(6),
                        "GOOD", HttpStatus.BAD_REQUEST,
                        "'startDateTimeInBed' must in the night period (greater than 18:00 or lesser than 06:00)"
                )
        );
    }
}
