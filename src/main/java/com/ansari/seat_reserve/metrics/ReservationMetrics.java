package com.ansari.seat_reserve.metrics;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class ReservationMetrics {

	private final Counter confirmed;
	private final Counter seatTaken;
	private final Counter limit;
	private final Counter replay;
	private final Counter otherDeclined;
	private final java.util.Map<Long, AtomicLong> availableByShow = new java.util.concurrent.ConcurrentHashMap<>();
	private final MeterRegistry registry;

	public ReservationMetrics(MeterRegistry r) {
		this.registry = r;
		confirmed = r.counter("reservations_confirmed_total");
		seatTaken = r.counter("reservations_declined_total", "reason", "seat-taken");
		limit = r.counter("reservations_declined_total", "reason", "per-user-limit");
		replay = r.counter("reservations_declined_total", "reason", "idempotent-replay");
		otherDeclined = r.counter("reservations_declined_total", "reason", "other");
	}

	public void confirmed() {
		confirmed.increment();
	}

	public void declined(String reason) {
		switch (reason) {
		case "seat-taken" -> seatTaken.increment();
		case "per-user-limit" -> limit.increment();
		case "idempotent-replay" -> replay.increment();
		default -> otherDeclined.increment();
		}
	}

	public void available(Long showId, long n) {
		AtomicLong value = availableByShow.computeIfAbsent(showId, id -> {
			AtomicLong holder = new AtomicLong();
			registry.gauge("seats_available", java.util.List.of(io.micrometer.core.instrument.Tag.of("show_id", String.valueOf(id))), holder, AtomicLong::doubleValue);
			return holder;
		});
		value.set(n);
	}
}
