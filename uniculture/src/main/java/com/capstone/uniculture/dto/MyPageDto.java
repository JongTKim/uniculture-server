package com.capstone.uniculture.dto;

import com.capstone.uniculture.entity.Gender;
import com.capstone.uniculture.entity.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MyPageDto {
    private Long id;
    private String email;
    private String nickname;
    private Integer age;
    private String gender;

    private String profileUrl;
    private List<String> myHobbyList;
    //private List<String> post;
    private List<String> myLanguages;
    private List<String> wantLanguages;
    private Integer friendNum;

    public MyPageDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = getNickname();
        this.age = member.getAge();
        this.gender = member.getGender().toString();
        this.profileUrl = member.getProfileUrl();
        this.myHobbyList = member.getMyHobbyList().stream().map(myHobby -> myHobby.getHobbyName()).collect(Collectors.toList());
        this.myLanguages = member.getMyLanguages().stream().map(myLanguage -> myLanguage.getLanguage()).collect(Collectors.toList());
        this.wantLanguages = member.getWantLanguages().stream().map(wantLanguage -> wantLanguage.getLanguage()).collect(Collectors.toList());
        this.friendNum = member.getFriends().size();
    }
}
