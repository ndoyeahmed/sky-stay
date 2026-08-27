package com.skystay.booking.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {

    private final ReservationId id;
    private final String hotelId;
    private final RoomNumber room;
    private final GuestEmail guest;
    private final DateRange stay;
    private final Money price;
    private Money penalty;
    private ReservationStatus status;

    private Reservation(ReservationId id, String hotelId, RoomNumber room, GuestEmail guest,
                         DateRange stay, Money price) {
        this.id = id;
        this.hotelId = hotelId;
        this.room = room;
        this.guest = guest;
        this.stay = stay;
        this.price = price;
        this.penalty = new Money(BigDecimal.ZERO, price.currency());
        this.status = ReservationStatus.PENDING;
    }

    public static Reservation request(String hotelId, RoomNumber room, GuestEmail guest,
                                       DateRange stay, Money price) {
        return new Reservation(null, hotelId, room, guest, stay, price);
    }

    public static Reservation reconstruct(ReservationId id, String hotelId, RoomNumber room, GuestEmail guest,
                                           DateRange stay, Money price, Money penalty, ReservationStatus status) {
        Reservation reservation = new Reservation(id, hotelId, room, guest, stay, price);
        reservation.penalty = penalty;
        reservation.status = status;
        return reservation;
    }

    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Seule une réservation en attente peut être confirmée.");
        }
        status = ReservationStatus.CONFIRMED;
    }

    public void cancel(LocalDate today) {
        if (status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Cette réservation est déjà annulée.");
        }
        long daysUntilStart = ChronoUnit.DAYS.between(today, stay.start());
        penalty = daysUntilStart < 2 ? price.percentage(30) : new Money(BigDecimal.ZERO, price.currency());
        status = ReservationStatus.CANCELLED;
    }

    public ReservationId id() {
        return id;
    }

    public String hotelId() {
        return hotelId;
    }

    public RoomNumber room() {
        return room;
    }

    public GuestEmail guest() {
        return guest;
    }

    public DateRange stay() {
        return stay;
    }

    public Money price() {
        return price;
    }

    public Money penalty() {
        return penalty;
    }

    public ReservationStatus status() {
        return status;
    }
}
