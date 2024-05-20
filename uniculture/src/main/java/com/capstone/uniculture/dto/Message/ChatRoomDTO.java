package com.capstone.uniculture.dto.Message;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {
  private Long id;
  // 상대 유저 이름
  private String username;
  // 가장 최근 메시지
  private String latestMessage;

  private MessageType messageType;
  // 그 메시지를 보낸시간
  private LocalDateTime latestMessageTime;
  // 안읽은 메시지의 개수
  private Long unreadCount;

  private Gender gender;
  private Integer age;

  private String profileurl;
  private String country;

}
