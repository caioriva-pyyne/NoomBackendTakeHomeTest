package com.noom.interview.fullstack.sleep.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noom.interview.fullstack.sleep.model.entity.AfterSleepFeeling;
import com.noom.interview.fullstack.sleep.validator.annotation.NotAFutureDateTime;
import com.noom.interview.fullstack.sleep.validator.annotation.WithTimeGreaterOrLesserThan;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
public class SleepLogCreateRequest {
    private static final String VALID_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @JsonProperty
    @NotNull
    @DateTimeFormat(pattern = VALID_DATE_TIME_FORMAT)
    @WithTimeGreaterOrLesserThan(greaterThan = "18:00", lesserThan = "06:00", message = "'startDateTimeInBed' must in the night period (greater than 18:00 or lesser than 06:00)")
    @NotAFutureDateTime(message = "'startDateTimeInBed' can not be in the future")
    private LocalDateTime startDateTimeInBed;

    @JsonProperty
    @NotNull
    @DateTimeFormat(pattern = VALID_DATE_TIME_FORMAT)
    @NotAFutureDateTime(message = "'endDateTimeInBed' can not be in the future")
    private LocalDateTime endDateTimeInBed;

    @JsonProperty
    @NotNull
    @Pattern(regexp = "OK|BAD|GOOD", message = "'feeling' must be one of the following: OK, BAD or GOOD")
    private String feeling;

    public AfterSleepFeeling getFeeling() {
        return AfterSleepFeeling.valueOf(feeling);
    }
}
