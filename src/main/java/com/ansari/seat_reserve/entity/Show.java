package com.ansari.seat_reserve.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="shows") @Getter @Setter @NoArgsConstructor
public class Show {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable=false, length=120) 
    private String name;

    @Column(name="price_paise", nullable=false) 
    private long pricePaise;

    @Column(name="per_user_limit", nullable=false) 
    private int perUserLimit = 4;

    @Column(name="created_at", nullable=false) 
    private Instant createdAt;

    @PrePersist void prePersist(){ 
        if(createdAt==null) createdAt=Instant.now(); 
    }
}
