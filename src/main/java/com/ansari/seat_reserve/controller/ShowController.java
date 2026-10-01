package com.ansari.seat_reserve.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ansari.seat_reserve.dto.CreateShowRequest;
import com.ansari.seat_reserve.dto.ShowResponse;
import com.ansari.seat_reserve.service.ShowService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/v1/shows") 
@RequiredArgsConstructor 
public class ShowController {
    private final ShowService service;
    
    @PostMapping 
    public ResponseEntity<ShowResponse> create(@Valid @RequestBody CreateShowRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/{id}") 
    public ShowResponse get(@PathVariable Long id){
        return service.get(id);
    }
}
