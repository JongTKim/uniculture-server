package com.capstone.uniculture.dto.Message;

import com.capstone.uniculture.entity.Message.ChatMessage;
import com.capstone.uniculture.entity.Message.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class ChatMessageDTO {
  private MessageType type;
  private Long roomId;
  private String sender;
  private String message;

  @Builder
  public ChatMessageDTO(MessageType type, Long roomId, String sender, String message) {
    this.type = type;
    this.roomId = roomId;
    this.sender = sender;
    this.message = message;
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
