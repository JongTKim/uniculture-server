package com.capstone.uniculture.message.dto;

import com.capstone.uniculture.message.entity.ChatMessage;
import com.capstone.uniculture.message.entity.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ChatMessageDTO {
  private MessageType type;
  private Long roomId;
  private String sender;
  private String message;
  private LocalDateTime time; //채팅 발송 시간
}
