package com.skystay.booking.infrastructure.persistence;

import com.skystay.booking.domain.model.DateRange;
import com.skystay.booking.domain.model.GuestEmail;
import com.skystay.booking.domain.model.Money;
import com.skystay.booking.domain.model.Reservation;
import com.skystay.booking.domain.model.ReservationId;
import com.skystay.booking.domain.model.ReservationStatus;
import com.skystay.booking.domain.model.RoomNumber;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private static final String CURRENCY = "XOF";

    public Reservation toDomain(ReservationEntity entity) {
        return Reservation.reconstruct(
                new ReservationId(entity.getId()),
                entity.getHotelId(),
                new RoomNumber(entity.getRoomNumber()),
                new GuestEmail(entity.getGuestEmail()),
                new DateRange(entity.getStartDate(), entity.getEndDate()),
                new Money(entity.getPrice(), CURRENCY),
                new Money(entity.getPenalty(), CURRENCY),
                ReservationStatus.valueOf(entity.getStatus()));
    }

    public ReservationEntity toEntity(Reservation reservation) {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(reservation.id() != null ? reservation.id().value() : null);
        entity.setHotelId(reservation.hotelId());
        entity.setRoomNumber(reservation.room().value());
        entity.setGuestEmail(reservation.guest().value());
        entity.setStartDate(reservation.stay().start());
        entity.setEndDate(reservation.stay().end());
        entity.setPrice(reservation.price().amount());
        entity.setPenalty(reservation.penalty().amount());
        entity.setStatus(reservation.status().name());
        return entity;
    }
}
