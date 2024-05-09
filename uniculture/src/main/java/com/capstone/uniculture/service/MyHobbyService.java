package com.capstone.uniculture.service;

import com.capstone.uniculture.entity.Member.MyHobby;
import com.capstone.uniculture.repository.MyHobbyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyHobbyService {

    private final MyHobbyRepository myHobbyRepository;

    public List<MyHobby> findAll() { return myHobbyRepository.findAll();}

    public void create(MyHobby myHobby){
        myHobbyRepository.save(myHobby);
    }

    public void createByList(List<MyHobby> myHobbyList){
        myHobbyRepository.saveAll(myHobbyList);
    }

    public void delete(MyHobby myHobby){
        myHobbyRepository.delete(myHobby);
    }

    public void deleteByList(List<MyHobby> myHobbyList){
        myHobbyRepository.deleteAll(myHobbyList);
    }

}
