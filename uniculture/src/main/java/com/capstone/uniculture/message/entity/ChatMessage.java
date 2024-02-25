package com.capstone.uniculture.message.entity;

import com.capstone.uniculture.message.dto.ChatMessageDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(columnList = "chatRoom_id")
})
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private MessageType type;

  @ManyToOne
  @JoinColumn(name = "chatRoom_id")
  private ChatRoom chatRoom;


  private String sender;

  private String message;
  private LocalDateTime time;
}
