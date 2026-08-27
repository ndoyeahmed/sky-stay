package com.skystay.booking.service;

import com.skystay.booking.persistence.Reservation;
import com.skystay.booking.persistence.ReservationRepository;
import com.skystay.booking.web.BookingRequest;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private static final BigDecimal PRICE_PER_NIGHT = BigDecimal.valueOf(50000);

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation book(BookingRequest request) {
        boolean overlaps = repository.existsOverlapping(
                request.hotelId(), request.roomNumber(), request.startDate(), request.endDate());
        if (overlaps) {
            throw new IllegalStateException("Cette chambre est déjà réservée sur cette période.");
        }
        long nights = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        Reservation reservation = new Reservation();
        reservation.setHotelId(request.hotelId());
        reservation.setRoomNumber(request.roomNumber());
        reservation.setGuestEmail(request.guestEmail());
        reservation.setStartDate(request.startDate());
        reservation.setEndDate(request.endDate());
        reservation.setPrice(PRICE_PER_NIGHT.multiply(BigDecimal.valueOf(nights)));
        reservation.setPenalty(BigDecimal.ZERO);
        reservation.setStatus("PENDING");
        return repository.save(reservation);
    }
}
