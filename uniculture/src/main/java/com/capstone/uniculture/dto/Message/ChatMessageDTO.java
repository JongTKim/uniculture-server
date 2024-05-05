package com.capstone.uniculture.dto.Message;

import com.capstone.uniculture.entity.Message.ChatMessage;
import com.capstone.uniculture.entity.Message.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class ChatMessageDTO {
  private MessageType type;
  private Long roomId; // 채팅방 id
  private String sender; // 발송자 이름(채팅방 이름)
  private Long receiver; // 상대 id
  private String message; // 메시지
  private LocalDateTime createdDate; // 시간

  @Builder
  public ChatMessageDTO(MessageType type, Long roomId, String sender, Long receiver, String message, LocalDateTime createdDate) {
    this.type = type;
    this.roomId = roomId;
    this.sender = sender;
    this.receiver = receiver;
    this.message = message;
    this.createdDate = createdDate;
  }

  // DTO -> Entity
  public static ChatMessage toChatMessage(ChatMessageDTO chatMessageDTO){
    return ChatMessage.builder()
            .type(chatMessageDTO.getType())
            .message(chatMessageDTO.getMessage())
            .build();
  }

  // Entity -> DTO

}
