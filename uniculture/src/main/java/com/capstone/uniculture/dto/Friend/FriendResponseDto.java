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

    @Builder
    public FriendResponseDto(Long id, String nickname, Integer age, Gender gender) {
        this.id = id;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
    }

    public static FriendResponseDto fromMember(Member member){
        return FriendResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .age(member.getAge())
                .gender(member.getGender())
                .build();
    }

}
