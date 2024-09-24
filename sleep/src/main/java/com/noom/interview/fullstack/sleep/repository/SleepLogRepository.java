package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.model.entity.SleepLog;
import com.noom.interview.fullstack.sleep.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, UUID> {
    Optional<SleepLog> findBySleepDateAndUser(LocalDate sleepDate, User user);

    List<SleepLog> findAllByUserAndSleepDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
