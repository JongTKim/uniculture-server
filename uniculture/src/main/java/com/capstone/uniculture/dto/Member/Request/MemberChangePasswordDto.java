package com.capstone.uniculture.dto.Member.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberChangePasswordDto {
    private String email;
    private String exPassword;
    private String newPassword;
}
