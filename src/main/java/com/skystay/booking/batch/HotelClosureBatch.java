package com.skystay.booking.batch;

import com.skystay.booking.persistence.Reservation;
import com.skystay.booking.persistence.ReservationRepository;
import org.springframework.stereotype.Component;

@Component
public class HotelClosureBatch {

    private final ReservationRepository repository;

    public HotelClosureBatch(ReservationRepository repository) {
        this.repository = repository;
    }

    public void cancelAllForHotel(String hotelId) {
        for (Reservation r : repository.findByHotelId(hotelId)) {
            r.setStatus("CANCELLED");
            repository.save(r);
        }
    }
}
