package com.ansari.seat_reserve.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="reservation_seats") 
@Getter 
@Setter 
@NoArgsConstructor
@IdClass(ReservationSeat.Key.class)
public class ReservationSeat {
    @Id 
    @Column(name="reservation_id") 
    private Long reservationId;

    @Id 
    @Column(name="seat_id") 
    private Long seatId;

    public ReservationSeat(Long reservationId, Long seatId){
        this.reservationId=reservationId;this.seatId=seatId;
    }

    @Data 
    @NoArgsConstructor 
    @AllArgsConstructor
    public static class Key implements Serializable { 
        private Long reservationId; 
        private Long seatId; 
    }

}
