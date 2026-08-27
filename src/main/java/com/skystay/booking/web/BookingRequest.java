package com.skystay.booking.web;

import java.time.LocalDate;

public record BookingRequest(
        String hotelId,
        String roomNumber,
        String guestEmail,
        LocalDate startDate,
        LocalDate endDate) {
}
