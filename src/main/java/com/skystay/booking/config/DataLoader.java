package com.skystay.booking.config;

import com.skystay.booking.infrastructure.persistence.ReservationEntity;
import com.skystay.booking.infrastructure.persistence.SpringDataReservationRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private static final int ROOMS = 40;
    private static final int STAYS_PER_ROOM = 10;

    private final SpringDataReservationRepository repository;

    public DataLoader(SpringDataReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        List<ReservationEntity> reservations = new ArrayList<>();
        int guestCounter = 0;
        for (int room = 1; room <= ROOMS; room++) {
            for (int stay = 0; stay < STAYS_PER_ROOM; stay++) {
                LocalDate start = LocalDate.now().plusDays(1 + stay * 4L);
                ReservationEntity reservation = new ReservationEntity();
                reservation.setHotelId("IBIS-DAKAR");
                reservation.setRoomNumber(String.valueOf(100 + room));
                reservation.setGuestEmail("guest" + guestCounter++ + "@skystay.test");
                reservation.setStartDate(start);
                reservation.setEndDate(start.plusDays(2));
                reservation.setPrice(BigDecimal.valueOf(100_000));
                reservation.setPenalty(BigDecimal.ZERO);
                reservation.setStatus("CONFIRMED");
                reservations.add(reservation);
            }
        }
        repository.saveAll(reservations);
    }
}
