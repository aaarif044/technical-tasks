package com.ansari.seat_reserve.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ansari.seat_reserve.dto.ReservationResponse;
import com.ansari.seat_reserve.dto.ReserveRequest;
import com.ansari.seat_reserve.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/v1") 
@RequiredArgsConstructor 
public class ReservationController {
	private final ReservationService service;

	@PostMapping("/shows/{id}/reserve") 
	public ResponseEntity<ReservationResponse> reserve(@PathVariable Long id,@Valid @RequestBody ReserveRequest req, Authentication auth){
		return ResponseEntity.status(HttpStatus.CREATED).body(service.reserve(id,auth.getName(),req));
	}

	@PostMapping("/reservations/{id}/cancel") 
	public ReservationResponse cancel(@PathVariable Long id,Authentication auth){
		return service.cancel(id,auth.getName());
	}
}
