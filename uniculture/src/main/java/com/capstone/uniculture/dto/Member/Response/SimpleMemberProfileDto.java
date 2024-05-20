package com.capstone.uniculture.dto.Member.Response;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SimpleMemberProfileDto {
    private Long id;
    private String nickname;
    private Integer age;
    private Gender gender;
    private String introduce;
    private Integer friendstatus; // 기본값은 2이다

    // -- 이미지 관련 --
    private String profileurl;
    private String country;

    public static SimpleMemberProfileDto fromMember(Member member){
        return SimpleMemberProfileDto.builder()
                .id(member.getId())
                .profileurl(member.getProfileUrl())
                .country(member.getCountry())
                .nickname(member.getNickname())
                .age(member.getAge())
                .gender(member.getGender())
                .introduce(member.getIntroduce())
                .friendstatus(2)
                .build();
    }

    public void changeStatue(Integer status){
        this.friendstatus = status;
    }
}
