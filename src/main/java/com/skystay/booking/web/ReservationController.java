package com.skystay.booking.web;

import com.skystay.booking.persistence.Reservation;
import com.skystay.booking.persistence.ReservationRepository;
import com.skystay.booking.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;
    private final ReservationRepository repository;

    public ReservationController(ReservationService service, ReservationRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<ReservationView> book(@RequestBody BookingRequest request) {
        Reservation reservation = service.book(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toView(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationView> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(reservation -> ResponseEntity.ok(toView(reservation)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationView> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(toView(service.cancel(id)));
    }

    private ReservationView toView(Reservation r) {
        return new ReservationView(r.getId(), r.getHotelId(), r.getRoomNumber(), r.getGuestEmail(),
                r.getStartDate(), r.getEndDate(), r.getPrice(), r.getPenalty(), r.getStatus());
    }
}
