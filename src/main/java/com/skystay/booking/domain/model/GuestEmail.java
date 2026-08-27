package com.skystay.booking.domain.model;

import java.util.regex.Pattern;

public record GuestEmail(String value) {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public GuestEmail {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("Adresse email invalide.");
        }
    }
}
