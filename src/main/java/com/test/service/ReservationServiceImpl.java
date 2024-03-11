package com.test.service;

import com.test.model.ReservationStatus;
import com.test.model.dto.CurrentBooking;
import com.test.model.entity.MobilePhoneReservation;
import com.test.model.entity.ReservationOutbox;
import com.test.repository.MobilePhoneReservationRepository;
import com.test.repository.ReservationOutboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private final MobilePhoneReservationRepository mobilePhoneReservationRepository;

    @Autowired
    private final ReservationOutboxRepository reservationOutboxRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public int reserve(int phoneId, int userId) {
        final var mobilePhoneReservation = new MobilePhoneReservation();
        mobilePhoneReservation.setPhoneId(phoneId);
        mobilePhoneReservation.setUserId(userId);
        mobilePhoneReservation.setCreatedAt(LocalDateTime.now());
        final var savedReservation = mobilePhoneReservationRepository.save(mobilePhoneReservation);

        final var reservationOutbox = new ReservationOutbox();
        reservationOutbox.setPhoneId(savedReservation.getPhoneId());
        reservationOutbox.setUserId(userId);
        reservationOutbox.setReservationStatus(ReservationStatus.BOOKED);
        reservationOutbox.setCreatedAt(mobilePhoneReservation.getCreatedAt());
        reservationOutboxRepository.save(reservationOutbox);

        return savedReservation.getPhoneId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void release(int phoneId, int userId) {
        final var mobilePhoneReservation = mobilePhoneReservationRepository.findOneByPhoneIdAndUserId(phoneId, userId);
        if(mobilePhoneReservation != null) {
            final var reservationOutbox = new ReservationOutbox();
            reservationOutbox.setPhoneId(mobilePhoneReservation.getPhoneId());
            reservationOutbox.setUserId(userId);
            reservationOutbox.setReservationStatus(ReservationStatus.RELEASED);
            reservationOutbox.setCreatedAt(mobilePhoneReservation.getCreatedAt());
            reservationOutboxRepository.save(reservationOutbox);
            mobilePhoneReservationRepository.delete(mobilePhoneReservation);
        } else throw new IllegalArgumentException("Reservation not found");
    }

    @Override
    public CurrentBooking currentBooking(int phoneId) {
        final var reservation =  mobilePhoneReservationRepository.findOneByPhoneId(phoneId);
        if(reservation != null) {
            return new CurrentBooking(
                    reservation.getUserId(),
                    reservation.getPhoneId(),
                    reservation.getCreatedAt()
            );
        }
        return null;
    }
}
