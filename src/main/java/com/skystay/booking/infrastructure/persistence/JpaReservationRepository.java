package com.skystay.booking.infrastructure.persistence;

import com.skystay.booking.domain.model.DateRange;
import com.skystay.booking.domain.model.Reservation;
import com.skystay.booking.domain.model.ReservationId;
import com.skystay.booking.domain.model.RoomNumber;
import com.skystay.booking.domain.port.ReservationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaReservationRepository implements ReservationRepository {

    private final SpringDataReservationRepository springData;
    private final ReservationMapper mapper;

    public JpaReservationRepository(SpringDataReservationRepository springData, ReservationMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<Reservation> findById(ReservationId id) {
        return springData.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Reservation> findByHotelId(String hotelId) {
        return springData.findByHotelId(hotelId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Reservation save(Reservation reservation) {
        return mapper.toDomain(springData.save(mapper.toEntity(reservation)));
    }

    @Override
    public boolean overlaps(String hotelId, RoomNumber room, DateRange stay) {
        return springData.existsOverlapping(hotelId, room.value(), stay.start(), stay.end());
    }
}
