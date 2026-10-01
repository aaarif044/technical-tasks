package com.ansari.seat_reserve.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="idempotency_keys", uniqueConstraints=@UniqueConstraint(name="uq_idempotency_user_show_key", columnNames={"user_id","show_id","idempotency_key"}))
@Getter 
@Setter 
@NoArgsConstructor
public class IdempotencyKey {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;

    @Column(name="user_id", nullable=false, length=120) 
    private String userId;

    @ManyToOne(fetch=FetchType.LAZY, optional=false) 
    @JoinColumn(name="show_id", nullable=false) 
    private Show show;

    @Column(name="idempotency_key", nullable=false, length=200) 
    private String idempotencyKey;

    @Column(name="request_hash", nullable=false, length=64) 
    private String requestHash;

    @Column(name="reservation_id", nullable=false) 
    private Long reservationId;

    @Column(name="created_at", nullable=false) 
    private Instant createdAt;

    @PrePersist void prePersist(){if(createdAt==null) createdAt=Instant.now();}
}
