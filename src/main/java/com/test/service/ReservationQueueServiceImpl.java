package com.test.service;

import com.test.model.dto.ReservationNotification;
import com.test.repository.ReservationOutboxRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@Setter
public class ReservationQueueServiceImpl {
    @Value("${spring.kafka.topic.reservation}")
    private String reservationTopic;

    @Autowired
    private ReservationOutboxRepository reservationOutboxRepository;

    @Autowired
    private KafkaTemplate<String, ReservationNotification> kafkaTemplate;

    @Scheduled(fixedDelayString = "${spring.kafka.topic.reservation.millis}")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deliverMessages(){
        reservationOutboxRepository.findAll().stream().map(msg -> new ReservationNotification(
                msg.getMsgId(),
                msg.getPhoneId(),
                msg.getCreatedAt(),
                msg.getUserId(),
                msg.getReservationStatus()
        )).forEach(msg -> kafkaTemplate.send(reservationTopic, msg));
        reservationOutboxRepository.deleteAll();
    }
}