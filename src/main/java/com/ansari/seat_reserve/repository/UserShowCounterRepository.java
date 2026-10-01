package com.ansari.seat_reserve.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.ansari.seat_reserve.entity.UserShowCounter;

import jakarta.persistence.LockModeType;

public interface UserShowCounterRepository extends JpaRepository<UserShowCounter, UserShowCounter.Key>{
    
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserShowCounter> findByShowIdAndUserId(Long showId,String userId);
}
