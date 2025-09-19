package com.example.management_selection_admin_seek.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * Service for client-related calculations and business logic
 * Handles life expectancy, retirement dates, and other derived calculations
 * Separated from mapping logic following Single Responsibility Principle
 */
@Service
@Slf4j
public class ClientCalculationService {

    // Business constants
    private static final int RETIREMENT_AGE = 65;
    private static final int AVERAGE_LIFE_EXPECTANCY = 78;
    private static final int EXTENDED_LIFE_YEARS = 5;

    /**
     * Calculate current age based on birth date
     */
    public int calculateCurrentAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Calculate estimated retirement date (65 years old)
     */
    public LocalDate calculateRetirementDate(LocalDate birthDate) {
        int currentAge = calculateCurrentAge(birthDate);
        
        if (currentAge >= RETIREMENT_AGE) {
            log.debug("Client already reached retirement age");
            return LocalDate.now(); // Already retired
        }
        
        int yearsToRetirement = RETIREMENT_AGE - currentAge;
        LocalDate retirementDate = LocalDate.now().plusYears(yearsToRetirement);
        
        log.debug("Calculated retirement date: {} (in {} years)", retirementDate, yearsToRetirement);
        return retirementDate;
    }

    /**
     * Calculate estimated life expectancy date (78 years average)
     */
    public LocalDate calculateLifeExpectancy(LocalDate birthDate) {
        int currentAge = calculateCurrentAge(birthDate);
        
        if (currentAge >= AVERAGE_LIFE_EXPECTANCY) {
            log.debug("Client exceeded average life expectancy, adding {} years", EXTENDED_LIFE_YEARS);
            return LocalDate.now().plusYears(EXTENDED_LIFE_YEARS);
        }
        
        int remainingYears = AVERAGE_LIFE_EXPECTANCY - currentAge;
        LocalDate lifeExpectancyDate = LocalDate.now().plusYears(remainingYears);
        
        log.debug("Calculated life expectancy: {} (in {} years)", lifeExpectancyDate, remainingYears);
        return lifeExpectancyDate;
    }

    /**
     * Calculate years to retirement
     */
    public int calculateYearsToRetirement(LocalDate birthDate) {
        int currentAge = calculateCurrentAge(birthDate);
        return Math.max(0, RETIREMENT_AGE - currentAge);
    }

    /**
     * Calculate estimated remaining years of life
     */
    public int calculateRemainingYears(LocalDate birthDate) {
        int currentAge = calculateCurrentAge(birthDate);
        return Math.max(0, AVERAGE_LIFE_EXPECTANCY - currentAge);
    }
}
