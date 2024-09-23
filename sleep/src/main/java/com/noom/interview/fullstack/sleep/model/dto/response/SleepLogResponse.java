package com.noom.interview.fullstack.sleep.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noom.interview.fullstack.sleep.model.entity.AfterSleepFeeling;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class SleepLogResponse {
    @JsonProperty
    private LocalDate sleepDate;

    @JsonProperty
    private LocalTime timeInBedStart;

    @JsonProperty
    private LocalTime timeInBedEnd;

    @JsonProperty
    private Duration totalTimeInBed;

    @JsonProperty
    private AfterSleepFeeling feeling;
}
