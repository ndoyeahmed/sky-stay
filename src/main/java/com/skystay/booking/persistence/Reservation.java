package com.skystay.booking.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hotelId;
    private String roomNumber;
    private String guestEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private BigDecimal penalty;
    private String status;

    public Reservation(String hotelId, String roomNumber, String guestEmail, LocalDate startDate, LocalDate endDate, BigDecimal penalty) {
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.guestEmail = guestEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        this.price = (BigDecimal.valueOf(50000).multiply(BigDecimal.valueOf(nights)));
        this.penalty = penalty;
        this.status = "PENDING";
    }

    public void cancelReservation() {
        long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), this.startDate);
        this.penalty = daysUntilStart < 2
                ? this.price.multiply(BigDecimal.valueOf(30)).divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        this.status = "CANCELLED";
    }

    public Long getId() {
        return id;
    }

    public String getHotelId() {
        return hotelId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getGuestEmail() {
        return guestEmail;
    }


    public LocalDate getStartDate() {
        return startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public BigDecimal getPenalty() {
        return penalty;
    }

    public String getStatus() {
        return status;
    }
}
