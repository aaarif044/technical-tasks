package com.ansari.seat_reserve.exception;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ApiException.class) ResponseEntity<?> api(ApiException e,HttpServletRequest r){
        return ResponseEntity.status(e.status()).body(Map.of("timestamp",Instant.now(),"status",e.status().value(),"code",e.code(),"message",e.getMessage(),"path",r.getRequestURI()));
    }

    @ExceptionHandler(Exception.class) ResponseEntity<?> generic(Exception e,HttpServletRequest r){
        return ResponseEntity.status(500).body(Map.of("timestamp",Instant.now(),"status",500,"code","INTERNAL_ERROR","message","Internal server error","path",r.getRequestURI()));
    }
}
