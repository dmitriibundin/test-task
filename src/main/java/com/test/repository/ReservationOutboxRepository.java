package com.test.repository;

import com.test.model.entity.ReservationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationOutboxRepository extends JpaRepository<ReservationOutbox, Integer> { }
