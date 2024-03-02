package com.capstone.uniculture.dto.Message;

import com.capstone.uniculture.entity.Message.ChatMessage;
import com.capstone.uniculture.entity.Message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.Message;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageResponseDto {
  private Long chatMessageId;
  private MessageType messageType;
  private String sender;
  private String message;
  private LocalDateTime createDate; // * 중요, 채팅 발송 시간

  @Builder
  public MessageResponseDto(Long chatMessageId, MessageType messageType, String sender, String message, LocalDateTime createDate) {
    this.chatMessageId = chatMessageId;
    this.messageType = messageType;
    this.sender = sender;
    this.message = message;
    this.createDate = createDate;
  }

  public static MessageResponseDto fromEntity(ChatMessage chatMessage){
    return MessageResponseDto.builder()
            .chatMessageId(chatMessage.getId())
            .messageType(chatMessage.getType())
            .sender(chatMessage.getMember().getNickname())
            .message(chatMessage.getMessage())
            .createDate(chatMessage.getCreatedDate())
            .build();
  }
}
