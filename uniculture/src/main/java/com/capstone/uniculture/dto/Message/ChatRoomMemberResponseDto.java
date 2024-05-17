package com.capstone.uniculture.dto.Message;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomMemberResponseDto {

    private Long id;
    private String name;
    private Integer age;
    private Gender gender;
    private String profileImage;

    public static ChatRoomMemberResponseDto fromEntity(Member member){
        return ChatRoomMemberResponseDto.builder()
                .id(member.getId())
                .name(member.getNickname())
                .age(member.getAge())
                .gender(member.getGender())
                .profileImage(member.getProfileUrl())
                .build();
    }
}
