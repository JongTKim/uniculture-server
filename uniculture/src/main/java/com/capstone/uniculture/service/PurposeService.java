package com.capstone.uniculture.service;

import com.capstone.uniculture.entity.Member.MyHobby;
import com.capstone.uniculture.entity.Member.Purpose;
import com.capstone.uniculture.repository.PurposeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurposeService {

    private final PurposeRepository purposeRepository;

    public void createByList(List<Purpose> purposeList){
        purposeList.stream().forEach(purpose -> purposeRepository.save(purpose));
    }
}
