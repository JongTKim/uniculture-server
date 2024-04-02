package com.capstone.uniculture.dto.Member;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.MyLanguage;
import com.capstone.uniculture.entity.Member.WantLanguage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UpdateProfileDto {

    private String profileUrl;
    private String introduce; // 자기소개
    private List<String> myHobbyList; // 취미
    private Map<String, Integer> myLanguages; // 언어
    private Map<String, Integer> wantLanguage; // 배우고 싶은 언어

    public UpdateProfileDto(Member member) {
        this.profileUrl = member.getProfileUrl();
        this.introduce = member.getIntroduce();
        this.myHobbyList = member.getMyHobbyList().stream().map(myHobby -> myHobby.getHobbyName()).collect(Collectors.toList());
        this.myLanguages = member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel));
        this.wantLanguage = member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel));
    }
}
