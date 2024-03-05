package com.capstone.uniculture.dto.Message;

import lombok.Getter;

@Getter
public class ChatRoomIdResponseDto {

    private final Long chatRoomId;

    public ChatRoomIdResponseDto(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
