package com.test.model.dto;

import java.time.LocalDateTime;

public record CurrentBooking(int userId, int phoneId, LocalDateTime bookedAt) { }
