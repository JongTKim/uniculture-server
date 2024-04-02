package com.capstone.uniculture.dto.Message;

import com.capstone.uniculture.dto.Member.MemberResponseDto;
import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
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

    public static ChatRoomMemberResponseDto fromEntity(Member member){
        return ChatRoomMemberResponseDto.builder()
                .id(member.getId())
                .name(member.getNickname())
                .age(member.getAge())
                .gender(member.getGender())
                .build();
    }
}
