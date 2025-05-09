package com.bnpl.application.service.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreditLineCalculatorTest {

    private CreditLineCalculator creditLineCalculator;

    @BeforeEach
    void setUp() {
        creditLineCalculator = new CreditLineCalculator();
    }

    @ParameterizedTest
    @CsvSource({
            "18, 3000.00",
            "21, 3000.00",
            "25, 3000.00",
            "26, 5000.00",
            "30, 5000.00",
            "31, 8000.00",
            "45, 8000.00",
            "65, 8000.00"
    })
    void shouldCalculateCreditLineBasedOnAge(int age, BigDecimal expectedAmount) {
        // Given
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = currentDate.minusYears(age);

        // When
        BigDecimal result = creditLineCalculator.calculateCreditLine(birthDate, currentDate);

        // Then
        assertEquals(expectedAmount, result);
    }

    @Test
    void shouldThrowExceptionWhenAgeIsBelow18() {
        // Given
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = currentDate.minusYears(17);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditLineCalculator.calculateCreditLine(birthDate, currentDate)
        );
        assertEquals("Age must be between 18 and 65 years", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAgeIsAbove65() {
        // Given
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = currentDate.minusYears(66);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditLineCalculator.calculateCreditLine(birthDate, currentDate)
        );
        assertEquals("Age must be between 18 and 65 years", exception.getMessage());
    }
}