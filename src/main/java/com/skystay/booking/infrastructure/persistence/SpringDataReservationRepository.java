package com.skystay.booking.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByHotelId(String hotelId);

    @Query("""
            SELECT COUNT(r) > 0 FROM ReservationEntity r
            WHERE r.hotelId = :hotelId AND r.roomNumber = :roomNumber
            AND r.startDate < :endDate AND r.endDate > :startDate
            """)
    boolean existsOverlapping(@Param("hotelId") String hotelId,
                               @Param("roomNumber") String roomNumber,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}
