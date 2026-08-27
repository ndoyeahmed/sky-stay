package com.skystay.booking.batch;

import com.skystay.booking.domain.model.Reservation;
import com.skystay.booking.domain.port.ReservationRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class HotelClosureBatch {

    private final ReservationRepository repository;

    public HotelClosureBatch(ReservationRepository repository) {
        this.repository = repository;
    }

    public void cancelAllForHotel(String hotelId) {
        for (Reservation r : repository.findByHotelId(hotelId)) {
            r.cancel(LocalDate.now());
            repository.save(r);
        }
    }
}
