package com.capstone.uniculture.dto.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberJoinDto {
    private String username;
    private String password;
    private String role;
}
