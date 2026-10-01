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

@Entity 
@Table(name="user_show_counters")
@Getter 
@Setter 
@NoArgsConstructor
@IdClass(UserShowCounter.Key.class)
public class UserShowCounter {
    @Id 
    @Column(name="show_id")
    private Long showId;

    @Id 
    @Column(name="user_id")
    private String userId;

    @Column(name="confirmed_seats", nullable=false) 
    private int confirmedSeats;

    public UserShowCounter(Long showId,String userId){
        this.showId=showId;
        this.userId=userId;
    }
    
    @Data 
    @NoArgsConstructor 
    @AllArgsConstructor
    public static class Key implements Serializable { 
        private Long showId; 
        private String userId; 
    }

}
