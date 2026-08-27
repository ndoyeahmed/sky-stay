package com.skystay.booking.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private Reservation reservationStartingIn(LocalDate today, long daysUntilStart) {
        LocalDate start = today.plusDays(daysUntilStart);
        DateRange stay = new DateRange(start, start.plusDays(2));
        Money price = new Money(BigDecimal.valueOf(100_000), "XOF");
        return Reservation.request("IBIS-DAKAR", new RoomNumber("101"),
                new GuestEmail("guest@skystay.test"), stay, price);
    }

    @Test
    void cancelTenDaysBeforeArrivalHasNoPenalty() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        Reservation reservation = reservationStartingIn(today, 10);
        reservation.cancel(today);
        assertThat(reservation.penalty().amount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void cancelOneDayBeforeArrivalAppliesThirtyPercentPenalty() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        Reservation reservation = reservationStartingIn(today, 1);
        reservation.cancel(today);
        assertThat(reservation.penalty().amount()).isEqualByComparingTo("30000.00");
    }

    @Test
    void cannotCancelTwice() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        Reservation reservation = reservationStartingIn(today, 10);
        reservation.cancel(today);
        assertThrows(IllegalStateException.class, () -> reservation.cancel(today));
    }
}
