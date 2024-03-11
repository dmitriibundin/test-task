package com.test.model.entity;

import com.test.model.ReservationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ReservationOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int msgId;

    private LocalDateTime createdAt;

    private ReservationStatus reservationStatus;

    private int phoneId;

    private int userId;
}
