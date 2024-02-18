package com.capstone.uniculture.service;


import com.capstone.uniculture.entity.Member.MyLanguage;
import com.capstone.uniculture.repository.MyLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyLanguageService {

    private final MyLanguageRepository myLanguageRepository;

    public List<MyLanguage> findAll() { return myLanguageRepository.findAll();}

    public void create(MyLanguage myLanguage){
        myLanguageRepository.save(myLanguage);
    }

    public void createByList(List<MyLanguage> myLanguages){
        myLanguages.stream().forEach(myLanguage -> myLanguageRepository.save(myLanguage));
    }

    public void delete(MyLanguage myLanguage){
        myLanguageRepository.delete(myLanguage);
    }

    public void deleteByList(List<MyLanguage> myLanguages){
        myLanguages.stream().forEach(myLanguage -> myLanguageRepository.delete(myLanguage));
    }

}
