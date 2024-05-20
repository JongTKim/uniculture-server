package com.capstone.uniculture.dto.Member.Request;

import com.capstone.uniculture.entity.Member.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UpdateProfileDto {

    private String introduce; // 자기소개
    private String profileurl; // 프로필 사진
    private List<String> purpose;
    private String mainPurpose;
    private List<String> myHobbyList; // 취미
    private Map<String, Integer> myLanguages; // 언어
    private Map<String, Integer> wantLanguage; // 배우고 싶은 언어


    public UpdateProfileDto(Member member) {
        this.profileurl = member.getProfileUrl();
        this.introduce = member.getIntroduce();
        this.myHobbyList = member.getMyHobbyList().stream().map(MyHobby::getHobbyName).toList();
        this.myLanguages = member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel));
        this.wantLanguage = member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel));
        this.mainPurpose = member.getMainPurpose();
        this.purpose = member.getPurpose().stream().map(Purpose::getPurpose).toList();
    }
}
