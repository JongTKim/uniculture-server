package com.capstone.uniculture.dto.Friend;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 내 친구 간단 조회 DTO -> 많은 정보가 필요없다. 다른 테이블과 JOIN 필요없다
 */
@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SimpleFriendResponseDto {

    private Long id;
    private String nickname;
    private Integer age;
    private Gender gender;

    // -- 이미지 관련 --
    private String profileurl;
    private String country;

    public static SimpleFriendResponseDto fromMember(Member member){
        return SimpleFriendResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .age(member.getAge())
                .gender(member.getGender())
                .profileurl(member.getProfileUrl())
                .country(member.getCountry())
                .build();
    }

}
