package com.ansari.seat_reserve.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ansari.seat_reserve.dto.CreateShowRequest;
import com.ansari.seat_reserve.dto.SeatResponse;
import com.ansari.seat_reserve.dto.ShowResponse;
import com.ansari.seat_reserve.entity.Seat;
import com.ansari.seat_reserve.entity.SeatStatus;
import com.ansari.seat_reserve.entity.Show;
import com.ansari.seat_reserve.exception.ApiException;
import com.ansari.seat_reserve.metrics.ReservationMetrics;
import com.ansari.seat_reserve.repository.SeatRepository;
import com.ansari.seat_reserve.repository.ShowRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ShowService {
 
    private final ShowRepository shows; 
    private final SeatRepository seats; 
    private final ReservationMetrics metrics;

    @Transactional 
    public ShowResponse create(CreateShowRequest req){
    List<String> names=req.seats().stream().map(String::trim).distinct().sorted().toList();

    if(names.size()!=req.seats().size())
        throw new ApiException(HttpStatus.CONFLICT,"DUPLICATE_SEAT","Seat names must be unique");

    Show s=new Show();
    s.setName(req.name());
    s.setPricePaise(req.price_paise());
    s.setPerUserLimit(req.per_user_limit()==null?4:req.per_user_limit());shows.save(s);
    for(String n:names){
        Seat seat=new Seat();
        seat.setShow(s);
        seat.setSeatNumber(n);seat.setStatus(SeatStatus.AVAILABLE);
        seats.save(seat);
    }
    return get(s.getId());
    }

    @Transactional(readOnly=true) 
    public ShowResponse get(Long id){
        
        Show s=shows.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"SHOW_NOT_FOUND","Show not found"));

        List<Seat> all=seats.findAllByShowId(id);
        long a=all.stream().filter(x->x.getStatus()==SeatStatus.AVAILABLE).count(),h=all.stream().filter(x->x.getStatus()==SeatStatus.HELD).count(),c=all.stream().filter(x->x.getStatus()==SeatStatus.CONFIRMED).count();
        metrics.available(id, a);
        
        return new ShowResponse(s.getId(),s.getName(),s.getPricePaise(),s.getPerUserLimit(),all.size(),a,h,c,all.stream().map(x->new SeatResponse(x.getSeatNumber(),x.getStatus().name().toLowerCase())).toList());
    }
}
