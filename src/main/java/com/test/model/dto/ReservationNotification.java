package com.test.model.dto;

import com.test.model.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationNotification(int messageId, int phoneId, LocalDateTime bookedAt, int userId, ReservationStatus reservationStatus) { }
