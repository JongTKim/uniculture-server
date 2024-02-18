package com.capstone.uniculture.service;

import com.capstone.uniculture.entity.Member.WantLanguage;
import com.capstone.uniculture.repository.WantLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WantLanguageService{

    private final WantLanguageRepository wantLanguageRepository;

    public List<WantLanguage> findAll() { return wantLanguageRepository.findAll();}

    public void create(WantLanguage wantLanguage){
        wantLanguageRepository.save(wantLanguage);
    }

    public void createByList(List<WantLanguage> wantLanguages){
        wantLanguages.stream().forEach(wantLanguage -> wantLanguageRepository.save(wantLanguage));
    }

    public void delete(WantLanguage wantLanguage){
        wantLanguageRepository.delete(wantLanguage);
    }

    public void deleteByList(List<WantLanguage> wantLanguages){
        wantLanguages.stream().forEach(wantLanguage -> wantLanguageRepository.delete(wantLanguage));
    }
}
