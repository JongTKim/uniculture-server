package com.capstone.uniculture.dto.Member;

import com.capstone.uniculture.entity.Member.Member;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    private String email;
    private String nickname;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}