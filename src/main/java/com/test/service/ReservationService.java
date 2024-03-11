package com.test.service;

import com.test.model.dto.CurrentBooking;

import java.util.Collection;
import java.util.Optional;

public interface ReservationService {
    int reserve(int phoneId, int userId);

    void release(int phoneId, int userId);

    CurrentBooking currentBooking(int phoneId);
}
