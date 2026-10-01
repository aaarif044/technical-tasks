package com.ansari.seat_reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ansari.seat_reserve.entity.Show;

public interface ShowRepository extends JpaRepository<Show,Long>{}
