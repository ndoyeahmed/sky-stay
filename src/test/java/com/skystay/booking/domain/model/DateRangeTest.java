package com.skystay.booking.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateRangeTest {

    @Test
    void rejectsEndBeforeOrEqualToStart() {
        LocalDate start = LocalDate.of(2026, 1, 10);
        LocalDate end = LocalDate.of(2026, 1, 5);
        assertThrows(InvalidStayException.class, () -> new DateRange(start, end));
    }

    @Test
    void computesNights() {
        DateRange stay = new DateRange(LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 15));
        assertThat(stay.nights()).isEqualTo(5);
    }
}
