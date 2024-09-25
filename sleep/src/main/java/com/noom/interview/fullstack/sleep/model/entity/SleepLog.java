package com.noom.interview.fullstack.sleep.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SleepLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private LocalDate sleepDate;

    @Column(nullable = false)
    private LocalDateTime dateTimeInBedStart;

    @Column(nullable = false)
    private LocalDateTime dateTimeInBedEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AfterSleepFeeling feeling;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
