package com.skystay.booking.domain.model;

public class InvalidStayException extends RuntimeException {

    public InvalidStayException(String message) {
        super(message);
    }
}
