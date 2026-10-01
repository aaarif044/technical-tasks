package com.ansari.seat_reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ansari.seat_reserve.entity.Reservation;
import com.ansari.seat_reserve.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation,Long>{
    long countByShowIdAndUserIdAndStatus(Long showId,String userId,ReservationStatus status);
    
    @Query("select count(rs) from ReservationSeat rs where rs.reservationId=:reservationId") long countSeats(Long reservationId);
}
