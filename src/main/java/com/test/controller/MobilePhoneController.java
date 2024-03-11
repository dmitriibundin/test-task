package com.test.controller;

import com.test.model.dto.CurrentBooking;
import com.test.model.dto.PhoneBookRequest;
import com.test.model.dto.PhoneInfo;
import com.test.model.dto.PhoneReleaseRequest;
import com.test.service.MobilePhoneService;
import com.test.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.awt.*;
import java.util.Set;

@RestController
public class MobilePhoneController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private static final Logger LOGGER = LoggerFactory.getLogger(MobilePhoneController.class);

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MobilePhoneService mobilePhoneService;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        LOGGER.error("Error", ex);
        return new ResponseEntity<>("Something went wrong, please try again", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(value = "/reserve")
    public ResponseEntity<Object> reserve(@RequestBody PhoneBookRequest bookRequest, @RequestHeader(value = USER_ID_HEADER, required = false) Integer userId) {
        if(userId == null){
            return new ResponseEntity<>("User is unauthenticated", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(reservationService.reserve(bookRequest.phoneId(), userId), HttpStatus.OK);
    }

    @PostMapping(value = "/release")
    public ResponseEntity<Object> release(@RequestBody PhoneReleaseRequest releaseRequest, @RequestHeader(value = USER_ID_HEADER, required = false) Integer userId) {
        if(userId == null){
            return new ResponseEntity<>("User is unauthenticated", HttpStatus.UNAUTHORIZED);
        }
        reservationService.release(releaseRequest.phoneId(), userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/phones")
    public Set<PhoneInfo> allPhoneNames() {
        return mobilePhoneService.allPhoneNames();
    }

    @GetMapping(value = "/booking")
    public  ResponseEntity<Object> currentBooking(int phoneId) {
        final var currenBooking = reservationService.currentBooking(phoneId);
        if(currenBooking == null){
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currenBooking, HttpStatus.OK);
    }
}