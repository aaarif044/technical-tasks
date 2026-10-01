package com.ansari.seat_reserve.dto;
import java.util.List;
public record ReservationResponse(Long reservation_id,Long show_id,String user_id,List<String> seats,long amount_paise,String status) {}
