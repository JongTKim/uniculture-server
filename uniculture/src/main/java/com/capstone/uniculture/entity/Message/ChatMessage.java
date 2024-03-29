package com.capstone.uniculture.entity.Message;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(columnList = "chatRoom_id")
})
public class ChatMessage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private MessageType type;

  @ManyToOne
  @JoinColumn(name = "chatRoom_id")
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  // 멤버쪽에서는 채팅 메시지를 전부 알필요는 없으므로, 단방향 관계로 정의
  private Member member;

  private String message;

  @Builder
  public ChatMessage(Long id, MessageType type, ChatRoom chatRoom, Member member, String message) {
    this.id = id;
    this.type = type;
    this.chatRoom = chatRoom;
    this.member = member;
    this.message = message;
  }
}
