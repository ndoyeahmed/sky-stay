package com.skystay.booking.web;

import com.skystay.booking.batch.HotelClosureBatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/hotels")
public class AdminController {

    private final HotelClosureBatch batch;

    public AdminController(HotelClosureBatch batch) {
        this.batch = batch;
    }

    @PostMapping("/{hotelId}/close")
    public void close(@PathVariable String hotelId) {
        batch.cancelAllForHotel(hotelId);
    }
}
