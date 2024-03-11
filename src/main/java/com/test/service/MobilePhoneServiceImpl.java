package com.test.service;

import com.test.model.dto.PhoneInfo;
import com.test.model.entity.MobilePhone;
import com.test.repository.MobilePhoneRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MobilePhoneServiceImpl implements MobilePhoneService{

    private final MobilePhoneRepository mobilePhoneRepository;

    @Override
    public Set<PhoneInfo> allPhoneNames() {
        return mobilePhoneRepository.findAll().stream().map(phone -> new PhoneInfo(phone.getPhoneId(), phone.getModelName())).collect(Collectors.toSet());
    }
}
