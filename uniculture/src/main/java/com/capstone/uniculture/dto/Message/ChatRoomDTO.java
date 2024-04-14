package com.capstone.uniculture.dto.Message;

import com.capstone.uniculture.entity.Message.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {
  private long id;
  private String name;
  // 가장 최근 메시지
  private String latestMessage;
  // 그 메시지를 보낸시간
  private LocalDateTime latestMessageTime;
  private int memberCount;

  // ChatRoom -> ChatRoomDTO (Response 시 사용)
  public static ChatRoomDTO fromEntity(ChatRoom chatRoom){
    return ChatRoomDTO.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .latestMessage(chatRoom.getLatestMessage())
            .latestMessageTime(chatRoom.getLatestMessageTime())
            .memberCount(chatRoom.getMemberships().size()) // 조심해야됨 고쳐야됨
            .build();
  }
}
