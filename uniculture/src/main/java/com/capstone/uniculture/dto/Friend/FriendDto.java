package com.capstone.uniculture.dto.Friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {
    private Long targetId; // 친구신청하는 사람 id
}
