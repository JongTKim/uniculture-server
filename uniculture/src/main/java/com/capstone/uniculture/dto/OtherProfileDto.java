package com.capstone.uniculture.dto;

import com.capstone.uniculture.entity.Member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtherProfileDto {
    private Long id;
    private String nickname;
    private Integer age;
    private String gender;
    private String introduce;

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
