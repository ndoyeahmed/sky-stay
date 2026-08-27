package com.skystay.booking.application;

import com.skystay.booking.domain.model.DateRange;
import com.skystay.booking.domain.model.GuestEmail;
import com.skystay.booking.domain.model.Money;
import com.skystay.booking.domain.model.Reservation;
import com.skystay.booking.domain.model.RoomNumber;
import com.skystay.booking.domain.port.ReservationRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookRoomUseCase {

    private static final Money PRICE_PER_NIGHT = new Money(BigDecimal.valueOf(50000), "XOF");

    private final ReservationRepository repository;

    public BookRoomUseCase(ReservationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Reservation book(String hotelId, RoomNumber room, GuestEmail guest, DateRange stay) {
        if (repository.overlaps(hotelId, room, stay)) {
            throw new IllegalStateException("Cette chambre est déjà réservée sur cette période.");
        }
        Money price = PRICE_PER_NIGHT.multiply(stay.nights());
        Reservation reservation = Reservation.request(hotelId, room, guest, stay, price);
        return repository.save(reservation);
    }
}
