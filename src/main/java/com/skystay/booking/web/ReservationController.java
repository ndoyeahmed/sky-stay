package com.skystay.booking.web;

import com.skystay.booking.application.BookRoomUseCase;
import com.skystay.booking.application.CancelReservationUseCase;
import com.skystay.booking.domain.model.DateRange;
import com.skystay.booking.domain.model.GuestEmail;
import com.skystay.booking.domain.model.Reservation;
import com.skystay.booking.domain.model.ReservationId;
import com.skystay.booking.domain.model.RoomNumber;
import com.skystay.booking.domain.port.ReservationRepository;
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

    private final BookRoomUseCase bookRoomUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final ReservationRepository repository;

    public ReservationController(BookRoomUseCase bookRoomUseCase,
                                  CancelReservationUseCase cancelReservationUseCase,
                                  ReservationRepository repository) {
        this.bookRoomUseCase = bookRoomUseCase;
        this.cancelReservationUseCase = cancelReservationUseCase;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<ReservationView> book(@RequestBody BookingRequest request) {
        DateRange stay = new DateRange(request.startDate(), request.endDate());
        Reservation reservation = bookRoomUseCase.book(request.hotelId(),
                new RoomNumber(request.roomNumber()), new GuestEmail(request.guestEmail()), stay);
        return ResponseEntity.status(HttpStatus.CREATED).body(toView(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationView> get(@PathVariable Long id) {
        return repository.findById(new ReservationId(id))
                .map(reservation -> ResponseEntity.ok(toView(reservation)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationView> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(toView(cancelReservationUseCase.cancel(id)));
    }

    private ReservationView toView(Reservation r) {
        return new ReservationView(r.id().value(), r.hotelId(), r.room().value(), r.guest().value(),
                r.stay().start(), r.stay().end(), r.price().amount(), r.penalty().amount(), r.status().name());
    }
}
