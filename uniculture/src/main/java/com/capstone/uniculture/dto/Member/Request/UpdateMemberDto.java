package com.capstone.uniculture.dto.Member.Request;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 개인정보를 변경할때 쓰이는 Dto

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberDto {
    private Integer age;
    private Gender gender;
    private String nickname;
    private String exPassword;
    private String newPassword;
    private String country;

    private String email;
    private Integer year;
    private Integer month;
    private Integer day;

    public UpdateMemberDto(Member member){
        this.age = member.getAge();
        this.gender = member.getGender();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.year = member.getBorn().getYear();
        this.month = member.getBorn().getMonthValue();
        this.day = member.getBorn().getDayOfMonth();
        this.country = member.getCountry();
    }
}
