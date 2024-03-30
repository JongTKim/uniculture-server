package com.capstone.uniculture.dto.Member.Response;

import com.capstone.uniculture.entity.Member.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherProfileDto {
    // 아이디
    private Long id;
    // 닉네임
    private String nickname;
    // 나이
    private Integer age;
    // 성별
    private String gender;
    // 한줄소개
    private String introduce;
    // 게시물 수
    private Integer postnum;
    // 친구 수
    private Integer friendnum;
    // 친구 여부
    private Boolean isfriend;
    // 언어능력

    // 취미

    public OtherProfileDto(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.age = member.getAge();
        if(member.getGender() != null){
            this.gender = member.getGender().toString();
        }
        this.introduce = member.getIntroduce();
    }
}
