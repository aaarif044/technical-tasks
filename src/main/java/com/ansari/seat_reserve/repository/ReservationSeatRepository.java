package com.ansari.seat_reserve.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ansari.seat_reserve.entity.ReservationSeat;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, ReservationSeat.Key>{
	List<ReservationSeat> findByReservationId(Long reservationId);
	void deleteByReservationId(Long reservationId);
}
