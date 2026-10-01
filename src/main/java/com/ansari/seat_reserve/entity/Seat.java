package com.ansari.seat_reserve.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="seats", uniqueConstraints=@UniqueConstraint(name="uq_show_seat", columnNames={"show_id","seat_number"}))
@Getter 
@Setter 
@NoArgsConstructor
public class Seat {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false) 
    @JoinColumn(name="show_id", nullable=false) 
    private Show show;


    @Column(name="seat_number", nullable=false, length=50) 
    private String seatNumber;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) 
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Column(name="updated_at", nullable=false) 
    private Instant updatedAt;

    @PrePersist 
    @PreUpdate void touch(){
        updatedAt=Instant.now();
    }
}
