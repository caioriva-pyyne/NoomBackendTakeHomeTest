package com.noom.interview.fullstack.sleep.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TimeManagementServiceImpl implements TimeManagementService {
    @Override
    public LocalDate getCurrentDay() {
        return LocalDate.now();
    }
}
