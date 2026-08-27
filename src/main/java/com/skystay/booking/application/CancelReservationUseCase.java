package com.skystay.booking.application;

import com.skystay.booking.domain.model.Reservation;
import com.skystay.booking.domain.model.ReservationId;
import com.skystay.booking.domain.port.ReservationRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelReservationUseCase {

    private final ReservationRepository repository;

    public CancelReservationUseCase(ReservationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Reservation cancel(Long id) {
        Reservation reservation = repository.findById(new ReservationId(id))
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));
        reservation.cancel(LocalDate.now());
        return repository.save(reservation);
    }
}
