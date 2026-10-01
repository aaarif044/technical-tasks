package com.ansari.seat_reserve.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ansari.seat_reserve.entity.IdempotencyKey;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey,Long> { 
	Optional<IdempotencyKey> findByUserIdAndShowIdAndIdempotencyKey(String userId,Long showId,String key); 
}
