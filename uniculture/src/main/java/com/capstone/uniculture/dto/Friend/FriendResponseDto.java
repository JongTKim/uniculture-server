package com.capstone.uniculture.dto.Friend;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.MyLanguage;
import com.capstone.uniculture.entity.Member.WantLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class FriendResponseDto {

    private Long id;
    // private String profileUrl;
    private String nickname;
    private Integer age;
    private Gender gender;
    private Map<String,Integer> myLanguages;
    private Map<String,Integer> wantLanguages;

    @Builder
    public FriendResponseDto(Long id, String nickname, Integer age, Gender gender, Map<String, Integer> myLanguages, Map<String, Integer> wantLanguages) {
        this.id = id;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.myLanguages = myLanguages;
        this.wantLanguages = wantLanguages;
    }

    public static FriendResponseDto fromMember(Member member){
        return FriendResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .age(member.getAge())
                .gender(member.getGender())
                .myLanguages(member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantLanguages(member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                .build();
    }

}
