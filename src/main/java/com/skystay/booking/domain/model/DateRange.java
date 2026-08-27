package com.skystay.booking.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DateRange(LocalDate start, LocalDate end) {

    public DateRange {
        if (!end.isAfter(start)) {
            throw new InvalidStayException("La date de fin doit être strictement postérieure à la date de début.");
        }
    }

    public long nights() {
        return ChronoUnit.DAYS.between(start, end);
    }
}
