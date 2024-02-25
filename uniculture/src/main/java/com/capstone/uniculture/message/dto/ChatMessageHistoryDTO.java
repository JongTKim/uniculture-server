package com.capstone.uniculture.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageHistoryDTO {
  private Long id;
  private String sender;
  private String message;
  private LocalDateTime time; //채팅 발송 시간
}
