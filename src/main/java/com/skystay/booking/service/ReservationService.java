package com.skystay.booking.service;

import com.skystay.booking.persistence.Reservation;
import com.skystay.booking.persistence.ReservationRepository;
import com.skystay.booking.web.BookingRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private static final BigDecimal PRICE_PER_NIGHT = BigDecimal.valueOf(50000);
    private static final BigDecimal PENALTY_RATE = BigDecimal.valueOf(30);

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
        Reservation reservation = new Reservation(request.hotelId(),
                request.roomNumber(),
                request.guestEmail(),
                request.startDate(),
                request.endDate(),
                BigDecimal.ZERO);
        return repository.save(reservation);
    }

    public Reservation cancel(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));
        reservation.cancelReservation();
        return repository.save(reservation);
    }

    public void cancelAll() {
        List<Reservation> reservations = repository.findAll();
        for (Reservation reservation : reservations) {
            reservation.cancelReservation();
        }
        repository.saveAll(reservations);
    }
}
