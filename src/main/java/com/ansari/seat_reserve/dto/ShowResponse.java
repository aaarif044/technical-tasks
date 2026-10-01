package com.ansari.seat_reserve.dto;
import java.util.List;
public record ShowResponse(Long id,String name,long price_paise,int per_user_limit,int total_seats,long available,long held,long confirmed,List<SeatResponse> seats) {}
