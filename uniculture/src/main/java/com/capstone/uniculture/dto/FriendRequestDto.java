package com.capstone.uniculture.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendRequestDto {
    private String toNickname; // 친구신청하는 사람 id
    private Long requestId; // 친구수락,거절할때 요청 자체의 id
}
