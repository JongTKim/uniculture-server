package com.capstone.uniculture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 개인정보를 변경할때 쓰이는 Dto

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberDto {
    private String nickname;
    private String exPassword;
    private String newPassword;
}
