package com.skystay.booking.domain.port;

import com.skystay.booking.domain.model.DateRange;
import com.skystay.booking.domain.model.Reservation;
import com.skystay.booking.domain.model.ReservationId;
import com.skystay.booking.domain.model.RoomNumber;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findById(ReservationId id);

    List<Reservation> findByHotelId(String hotelId);

    Reservation save(Reservation reservation);

    boolean overlaps(String hotelId, RoomNumber room, DateRange stay);
}
