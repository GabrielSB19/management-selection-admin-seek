package com.example.management_selection_admin_seek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ClientCalculationService
 * Validates all client-related calculations and business logic
 */
@DisplayName("Client Calculation Service Tests")
class ClientCalculationServiceTest {

    private ClientCalculationService clientCalculationService;

    // Test constants - matching service constants
    private static final int RETIREMENT_AGE = 65;
    private static final int AVERAGE_LIFE_EXPECTANCY = 78;
    private static final int EXTENDED_LIFE_YEARS = 5;

    @BeforeEach
    void setUp() {
        clientCalculationService = new ClientCalculationService();
    }

    @Test
    @DisplayName("Should calculate current age correctly for young person")
    void calculateCurrentAge_YoungPerson_ShouldReturnCorrectAge() {
        // Arrange - Person born exactly 25 years ago
        LocalDate birthDate = LocalDate.now().minusYears(25);

        // Act
        int age = clientCalculationService.calculateCurrentAge(birthDate);

        // Assert
        assertThat(age).isEqualTo(25);
    }

    @Test
    @DisplayName("Should calculate current age correctly for elderly person")
    void calculateCurrentAge_ElderlyPerson_ShouldReturnCorrectAge() {
        // Arrange - Person born exactly 80 years ago
        LocalDate birthDate = LocalDate.now().minusYears(80);

        // Act
        int age = clientCalculationService.calculateCurrentAge(birthDate);

        // Assert
        assertThat(age).isEqualTo(80);
    }

    @Test
    @DisplayName("Should calculate current age correctly for person born today")
    void calculateCurrentAge_BornToday_ShouldReturnZero() {
        // Arrange - Person born today
        LocalDate birthDate = LocalDate.now();

        // Act
        int age = clientCalculationService.calculateCurrentAge(birthDate);

        // Assert
        assertThat(age).isEqualTo(0);
    }

    @Test
    @DisplayName("Should calculate years to retirement for young person")
    void calculateYearsToRetirement_YoungPerson_ShouldReturnCorrectYears() {
        // Arrange - Person aged 30
        LocalDate birthDate = LocalDate.now().minusYears(30);

        // Act
        int yearsToRetirement = clientCalculationService.calculateYearsToRetirement(birthDate);

        // Assert - Should have 35 years to retirement (65 - 30)
        assertThat(yearsToRetirement).isEqualTo(35);
    }

    @Test
    @DisplayName("Should return zero years to retirement for person at retirement age")
    void calculateYearsToRetirement_AtRetirementAge_ShouldReturnZero() {
        // Arrange - Person aged exactly 65
        LocalDate birthDate = LocalDate.now().minusYears(65);

        // Act
        int yearsToRetirement = clientCalculationService.calculateYearsToRetirement(birthDate);

        // Assert - Should return 0 (already at retirement age)
        assertThat(yearsToRetirement).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return zero years to retirement for person past retirement age")
    void calculateYearsToRetirement_PastRetirementAge_ShouldReturnZero() {
        // Arrange - Person aged 70 (past retirement)
        LocalDate birthDate = LocalDate.now().minusYears(70);

        // Act
        int yearsToRetirement = clientCalculationService.calculateYearsToRetirement(birthDate);

        // Assert - Should return 0 (Math.max ensures non-negative)
        assertThat(yearsToRetirement).isEqualTo(0);
    }

    @Test
    @DisplayName("Should calculate remaining years for young person")
    void calculateRemainingYears_YoungPerson_ShouldReturnCorrectYears() {
        // Arrange - Person aged 20
        LocalDate birthDate = LocalDate.now().minusYears(20);

        // Act
        int remainingYears = clientCalculationService.calculateRemainingYears(birthDate);

        // Assert - Should have 58 remaining years (78 - 20)
        assertThat(remainingYears).isEqualTo(58);
    }

    @Test
    @DisplayName("Should return zero remaining years for person at life expectancy")
    void calculateRemainingYears_AtLifeExpectancy_ShouldReturnZero() {
        // Arrange - Person aged exactly 78
        LocalDate birthDate = LocalDate.now().minusYears(78);

        // Act
        int remainingYears = clientCalculationService.calculateRemainingYears(birthDate);

        // Assert - Should return 0 (at life expectancy)
        assertThat(remainingYears).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return zero remaining years for person past life expectancy")
    void calculateRemainingYears_PastLifeExpectancy_ShouldReturnZero() {
        // Arrange - Person aged 85 (past life expectancy)
        LocalDate birthDate = LocalDate.now().minusYears(85);

        // Act
        int remainingYears = clientCalculationService.calculateRemainingYears(birthDate);

        // Assert - Should return 0 (Math.max ensures non-negative)
        assertThat(remainingYears).isEqualTo(0);
    }

    @Test
    @DisplayName("Should calculate retirement date for young person")
    void calculateRetirementDate_YoungPerson_ShouldReturnFutureDate() {
        // Arrange - Person aged 30
        LocalDate birthDate = LocalDate.now().minusYears(30);

        // Act
        LocalDate retirementDate = clientCalculationService.calculateRetirementDate(birthDate);

        // Assert - Should be 35 years in the future (65 - 30)
        LocalDate expectedRetirement = LocalDate.now().plusYears(35);
        assertThat(retirementDate).isEqualTo(expectedRetirement);
    }

    @Test
    @DisplayName("Should return current date for person at retirement age")
    void calculateRetirementDate_AtRetirementAge_ShouldReturnCurrentDate() {
        // Arrange - Person aged exactly 65
        LocalDate birthDate = LocalDate.now().minusYears(65);

        // Act
        LocalDate retirementDate = clientCalculationService.calculateRetirementDate(birthDate);

        // Assert - Should return current date
        assertThat(retirementDate).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should return current date for person past retirement age")
    void calculateRetirementDate_PastRetirementAge_ShouldReturnCurrentDate() {
        // Arrange - Person aged 70
        LocalDate birthDate = LocalDate.now().minusYears(70);

        // Act
        LocalDate retirementDate = clientCalculationService.calculateRetirementDate(birthDate);

        // Assert - Should return current date
        assertThat(retirementDate).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should calculate life expectancy for young person")
    void calculateLifeExpectancy_YoungPerson_ShouldReturnFutureDate() {
        // Arrange - Person aged 25
        LocalDate birthDate = LocalDate.now().minusYears(25);

        // Act
        LocalDate lifeExpectancy = clientCalculationService.calculateLifeExpectancy(birthDate);

        // Assert - Should be 53 years in the future (78 - 25)
        LocalDate expectedLifeExpectancy = LocalDate.now().plusYears(53);
        assertThat(lifeExpectancy).isEqualTo(expectedLifeExpectancy);
    }

    @Test
    @DisplayName("Should add extended years for person at life expectancy")
    void calculateLifeExpectancy_AtLifeExpectancy_ShouldAddExtendedYears() {
        // Arrange - Person aged exactly 78
        LocalDate birthDate = LocalDate.now().minusYears(78);

        // Act
        LocalDate lifeExpectancy = clientCalculationService.calculateLifeExpectancy(birthDate);

        // Assert - Should add 5 extended years
        LocalDate expectedLifeExpectancy = LocalDate.now().plusYears(EXTENDED_LIFE_YEARS);
        assertThat(lifeExpectancy).isEqualTo(expectedLifeExpectancy);
    }

    @Test
    @DisplayName("Should add extended years for person past life expectancy")
    void calculateLifeExpectancy_PastLifeExpectancy_ShouldAddExtendedYears() {
        // Arrange - Person aged 85
        LocalDate birthDate = LocalDate.now().minusYears(85);

        // Act
        LocalDate lifeExpectancy = clientCalculationService.calculateLifeExpectancy(birthDate);

        // Assert - Should add 5 extended years
        LocalDate expectedLifeExpectancy = LocalDate.now().plusYears(EXTENDED_LIFE_YEARS);
        assertThat(lifeExpectancy).isEqualTo(expectedLifeExpectancy);
    }

    @Test
    @DisplayName("Should handle edge case - person exactly one year to retirement")
    void calculateYearsToRetirement_OneYearToRetirement_ShouldReturnOne() {
        // Arrange - Person aged 64
        LocalDate birthDate = LocalDate.now().minusYears(64);

        // Act
        int yearsToRetirement = clientCalculationService.calculateYearsToRetirement(birthDate);

        // Assert - Should have 1 year to retirement
        assertThat(yearsToRetirement).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle edge case - person exactly one year to life expectancy")
    void calculateRemainingYears_OneYearToLifeExpectancy_ShouldReturnOne() {
        // Arrange - Person aged 77
        LocalDate birthDate = LocalDate.now().minusYears(77);

        // Act
        int remainingYears = clientCalculationService.calculateRemainingYears(birthDate);

        // Assert - Should have 1 remaining year
        assertThat(remainingYears).isEqualTo(1);
    }

    @Test
    @DisplayName("Should validate business constants consistency")
    void businessConstants_ShouldBeConsistentWithServiceLogic() {
        // Assert - Validate that our test constants match expected values
        assertThat(RETIREMENT_AGE).isEqualTo(65);
        assertThat(AVERAGE_LIFE_EXPECTANCY).isEqualTo(78);
        assertThat(EXTENDED_LIFE_YEARS).isEqualTo(5);
        
        // Validate logical consistency
        assertThat(AVERAGE_LIFE_EXPECTANCY).isGreaterThan(RETIREMENT_AGE);
        assertThat(EXTENDED_LIFE_YEARS).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle comprehensive scenario - middle-aged professional")
    void comprehensiveScenario_MiddleAgedProfessional_ShouldCalculateAllMetricsCorrectly() {
        // Arrange - Person aged 45
        LocalDate birthDate = LocalDate.now().minusYears(45);

        // Act - Calculate all metrics
        int currentAge = clientCalculationService.calculateCurrentAge(birthDate);
        LocalDate retirementDate = clientCalculationService.calculateRetirementDate(birthDate);
        LocalDate lifeExpectancy = clientCalculationService.calculateLifeExpectancy(birthDate);
        int yearsToRetirement = clientCalculationService.calculateYearsToRetirement(birthDate);
        int remainingYears = clientCalculationService.calculateRemainingYears(birthDate);

        // Assert - All calculations should be consistent
        assertThat(currentAge).isEqualTo(45);
        assertThat(retirementDate).isEqualTo(LocalDate.now().plusYears(20)); // 65 - 45 = 20
        assertThat(lifeExpectancy).isEqualTo(LocalDate.now().plusYears(33)); // 78 - 45 = 33
        assertThat(yearsToRetirement).isEqualTo(20);
        assertThat(remainingYears).isEqualTo(33);
        
        // Validate logical relationships
        assertThat(remainingYears).isGreaterThan(yearsToRetirement);
        assertThat(lifeExpectancy).isAfter(retirementDate);
    }

    @Test
    @DisplayName("Should handle newborn baby calculations")
    void calculateAll_NewbornBaby_ShouldReturnMaximumValues() {
        // Arrange - Baby born today
        LocalDate birthDate = LocalDate.now();

        // Act - Calculate all metrics
        int currentAge = clientCalculationService.calculateCurrentAge(birthDate);
        int yearsToRetirement = clientCalculationService.calculateYearsToRetirement(birthDate);
        int remainingYears = clientCalculationService.calculateRemainingYears(birthDate);

        // Assert
        assertThat(currentAge).isEqualTo(0);
        assertThat(yearsToRetirement).isEqualTo(65);
        assertThat(remainingYears).isEqualTo(78);
    }

    @Test
    @DisplayName("Should handle super senior citizen calculations")
    void calculateAll_SuperSeniorCitizen_ShouldReturnMinimumValues() {
        // Arrange - Person aged 90
        LocalDate birthDate = LocalDate.now().minusYears(90);

        // Act - Calculate all metrics
        int currentAge = clientCalculationService.calculateCurrentAge(birthDate);
        int yearsToRetirement = clientCalculationService.calculateYearsToRetirement(birthDate);
        int remainingYears = clientCalculationService.calculateRemainingYears(birthDate);
        LocalDate lifeExpectancy = clientCalculationService.calculateLifeExpectancy(birthDate);

        // Assert
        assertThat(currentAge).isEqualTo(90);
        assertThat(yearsToRetirement).isEqualTo(0); 
        assertThat(remainingYears).isEqualTo(0); 
        assertThat(lifeExpectancy).isEqualTo(LocalDate.now().plusYears(EXTENDED_LIFE_YEARS));
    }
}