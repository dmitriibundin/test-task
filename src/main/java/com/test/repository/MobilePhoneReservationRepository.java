package com.test.repository;

import com.test.model.entity.MobilePhoneReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MobilePhoneReservationRepository extends JpaRepository<MobilePhoneReservation, Integer> {
    MobilePhoneReservation findOneByPhoneIdAndUserId(int phoneId, int userId);
    MobilePhoneReservation findOneByPhoneId(int phoneId);
}
