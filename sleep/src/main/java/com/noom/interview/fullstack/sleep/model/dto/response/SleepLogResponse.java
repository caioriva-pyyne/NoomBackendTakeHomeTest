package com.noom.interview.fullstack.sleep.model.dto.response;

import com.noom.interview.fullstack.sleep.model.entity.AfterSleepFeeling;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class SleepLogResponse {
    private LocalDate sleepDate;
    private LocalTime timeInBedStart;
    private LocalTime timeInBedEnd;
    private Duration totalTimeInBed;
    private AfterSleepFeeling feeling;
}
