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
    private String profileUrl;
    private String nickname;
    private Integer age;
    private String gender;
    //private List<String> post;
    private Integer friendNum;

    public MyPageDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = getNickname();
        this.age = member.getAge();
        this.gender = member.getGender().toString();
        this.profileUrl = member.getProfileUrl();
        this.friendNum = member.getFriends().size();
    }
}
