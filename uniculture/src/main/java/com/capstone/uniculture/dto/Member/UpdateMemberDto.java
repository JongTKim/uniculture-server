package com.capstone.uniculture.dto.Member;

import com.capstone.uniculture.entity.Member.Gender;
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
}
