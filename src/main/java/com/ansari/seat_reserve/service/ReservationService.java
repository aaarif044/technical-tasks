package com.ansari.seat_reserve.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ansari.seat_reserve.dto.ReservationResponse;
import com.ansari.seat_reserve.dto.ReserveRequest;
import com.ansari.seat_reserve.entity.IdempotencyKey;
import com.ansari.seat_reserve.entity.Reservation;
import com.ansari.seat_reserve.entity.ReservationSeat;
import com.ansari.seat_reserve.entity.ReservationStatus;
import com.ansari.seat_reserve.entity.Seat;
import com.ansari.seat_reserve.entity.SeatStatus;
import com.ansari.seat_reserve.entity.Show;
import com.ansari.seat_reserve.entity.UserShowCounter;
import com.ansari.seat_reserve.exception.ApiException;
import com.ansari.seat_reserve.metrics.ReservationMetrics;
import com.ansari.seat_reserve.repository.IdempotencyKeyRepository;
import com.ansari.seat_reserve.repository.ReservationRepository;
import com.ansari.seat_reserve.repository.ReservationSeatRepository;
import com.ansari.seat_reserve.repository.SeatRepository;
import com.ansari.seat_reserve.repository.ShowRepository;
import com.ansari.seat_reserve.repository.UserShowCounterRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ReservationService {

	private final ShowRepository shows; 
	private final SeatRepository seats; 
	private final ReservationRepository reservations; 
	private final ReservationSeatRepository reservationSeats; 
	private final IdempotencyKeyRepository keys; 
	private final UserShowCounterRepository counters; 
	private final JdbcTemplate jdbcTemplate; 
	private final ReservationMetrics metrics;

	@Transactional 
	public ReservationResponse reserve(Long showId,String userId,ReserveRequest req){
		lock(lockKey(showId,userId));
		lock(lockKey(showId,userId,req.idempotency_key()));

		Show show=shows.findById(showId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"SHOW_NOT_FOUND","Show not found"));

		List<String> requested=req.seats().stream().map(String::trim).distinct().sorted().toList();

		if(requested.size()!=req.seats().size())
			throw new ApiException(HttpStatus.CONFLICT,"DUPLICATE_SEAT","Duplicate seat in request");
		
		String hash=hash(requested);
		
		Optional<IdempotencyKey> existing=keys.findByUserIdAndShowIdAndIdempotencyKey(userId,showId,req.idempotency_key());
		if(existing.isPresent()){
			if(!existing.get().getRequestHash().equals(hash)){metrics.declined("other");
			throw new ApiException(HttpStatus.CONFLICT,"IDEMPOTENCY_KEY_REUSED","Same idempotency key was used with a different request");}metrics.declined("idempotent-replay");return response(reservations.findById(existing.get().getReservationId()).orElseThrow(),requested);}
			
			UserShowCounter counter=counters.findByShowIdAndUserId(showId,userId).orElseGet(()->counters.save(new UserShowCounter(showId,userId)));
			if(counter.getConfirmedSeats()+requested.size()>show.getPerUserLimit()){
				metrics.declined("per-user-limit");throw new ApiException(HttpStatus.CONFLICT,"PER_USER_LIMIT","Per-user seat limit exceeded");
			}

			List<Seat> locked=seats.findForUpdate(showId,requested);

			if(locked.size()!=requested.size()){metrics.declined("seat-taken");
			throw new ApiException(HttpStatus.CONFLICT,"SEAT_NOT_FOUND","One or more requested seats do not exist");}

			if(locked.stream().anyMatch(s->s.getStatus()!=SeatStatus.AVAILABLE)){metrics.declined("seat-taken");
			throw new ApiException(HttpStatus.CONFLICT,"SEAT_TAKEN","One or more requested seats are already taken");}

			Reservation r=new Reservation();
			
			r.setShow(show);
			r.setUserId(userId);
			r.setAmountPaise(show.getPricePaise()*requested.size());
			r.setStatus(ReservationStatus.CONFIRMED);reservations.save(r);

			for(Seat s:locked){s.setStatus(SeatStatus.CONFIRMED);reservationSeats.save(new ReservationSeat(r.getId(),s.getId()));

		}
			
		counter.setConfirmedSeats(counter.getConfirmedSeats()+requested.size()); 
		counters.save(counter);

		IdempotencyKey key=new IdempotencyKey();
		key.setUserId(userId);
		key.setShow(show);
		key.setIdempotencyKey(req.idempotency_key());
		key.setRequestHash(hash);
		key.setReservationId(r.getId());

		keys.save(key);
		metrics.confirmed();return response(r,requested);
	}

	@Transactional 
	public ReservationResponse cancel(Long id,String userId){
		
		Reservation r=reservations.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"RESERVATION_NOT_FOUND","Reservation not found"));
		
		if(!r.getUserId().equals(userId))
			throw new ApiException(HttpStatus.FORBIDDEN,"NOT_OWNER","Only the owner can cancel this reservation");
		
		lock(lockKey(r.getShow().getId(),userId));

		if(r.getStatus()==ReservationStatus.CANCELLED)
			return response(r,List.of());
		List<ReservationSeat> links=reservationSeats.findByReservationId(r.getId());
		List<Seat> owned=links.isEmpty()?List.of():seats.findForUpdateByIds(links.stream().map(ReservationSeat::getSeatId).toList()).stream().filter(s->s.getStatus()==SeatStatus.CONFIRMED).sorted(Comparator.comparing(Seat::getSeatNumber)).toList();
		
		for(Seat s:owned)s.setStatus(SeatStatus.AVAILABLE);

		reservationSeats.deleteAll(links);

		UserShowCounter counter=counters.findByShowIdAndUserId(r.getShow().getId(),userId).orElse(null);

		if(counter!=null){counter.setConfirmedSeats(Math.max(0,counter.getConfirmedSeats()-owned.size()));counters.save(counter);}

		r.setStatus(ReservationStatus.CANCELLED);r.setCancelledAt(Instant.now());return response(r,owned.stream().map(Seat::getSeatNumber).toList());}


		private ReservationResponse response(Reservation r,List<String> requested){List<String> ss=requested;
		if(requested.isEmpty())ss=seats.findAllByShowId(r.getShow().getId()).stream().filter(s->reservationSeats.findById(new ReservationSeat.Key(r.getId(),s.getId())).isPresent()).map(Seat::getSeatNumber).toList();return new ReservationResponse(r.getId(),r.getShow().getId(),r.getUserId(),ss,r.getAmountPaise(),r.getStatus().name().toLowerCase());}

		private void lock(long key) {jdbcTemplate.execute("SELECT pg_advisory_xact_lock(" + key + ")");}
		
		private long lockKey(Long showId,String userId){return lockKey(showId,userId,"USER");}

		private long lockKey(Long showId,String userId,String key){String input=showId+":"+userId+":"+key;byte[] b=hashBytes(input);long x=0;for(int i=0;
				i<8;
				i++)x=(x<<8)|(b[i]&0xffL);return x;}

		private byte[] hashBytes(String value){try{return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));}catch(Exception e){throw new IllegalStateException(e);}}

		private String hash(List<String> seats){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(String.join(",",seats).getBytes(StandardCharsets.UTF_8));StringBuilder x=new StringBuilder();for(byte v:b)x.append(String.format("%02x",v));return x.toString();}catch(Exception e){throw new IllegalStateException(e);}}
}
