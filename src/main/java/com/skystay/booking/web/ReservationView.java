package com.skystay.booking.web;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationView(
        Long id,
        String hotelId,
        String roomNumber,
        String guestEmail,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal price,
        BigDecimal penalty,
        String status) {
}
