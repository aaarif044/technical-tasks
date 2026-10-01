package com.ansari.seat_reserve.controller;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ansari.seat_reserve.dto.ApiResponse;

@RestController 
@RequestMapping("/api/v1/health") 
public class HealthController {

	@GetMapping("/live") 
	public ApiResponse<Map<String,String>> live(){
		return ApiResponse.ok(Map.of("status","UP"));
	}
}
