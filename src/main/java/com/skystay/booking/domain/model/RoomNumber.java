package com.skystay.booking.domain.model;

public record RoomNumber(String value) {

    public RoomNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le numéro de chambre est requis.");
        }
    }
}
