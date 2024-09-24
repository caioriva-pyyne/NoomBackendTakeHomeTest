package com.noom.interview.fullstack.sleep.model.dto.response;

import com.noom.interview.fullstack.sleep.model.entity.AfterSleepFeeling;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class SleepLogLastDaysAverageResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Duration averageTotalTimeInBed;
    private LocalTime averageTimeInBedStart;
    private LocalTime averageTimeInBedEnd;
    private Map<AfterSleepFeeling, Integer> feelingFrequencies;
}
