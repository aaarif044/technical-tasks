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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false) 
	@JoinColumn(name="show_id", nullable=false) 
	private Show show;
	
	@Column(name="user_id", nullable=false, length=120) 
	private String userId;
	
	@Column(name="amount_paise", nullable=false) 
	private long amountPaise;
	
	@Enumerated(EnumType.STRING) 
	@Column(nullable=false, length=20) 
	private ReservationStatus status;
	
	@Column(name="created_at", nullable=false) 
	private Instant createdAt;
	
	@Column(name="cancelled_at") 
	private Instant cancelledAt;
	
	@PrePersist void prePersist(){
		if(createdAt==null) createdAt=Instant.now();
	}
}
