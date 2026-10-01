package com.ansari.seat_reserve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ansari.seat_reserve.entity.Seat;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seat,Long>{
    @Query("select s from Seat s where s.show.id=:showId order by s.seatNumber") List<Seat> findAllByShowId(@Param("showId") Long showId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.show.id=:showId and s.seatNumber in :numbers order by s.seatNumber") List<Seat> findForUpdate(@Param("showId") Long showId,@Param("numbers") List<String> numbers);
}
