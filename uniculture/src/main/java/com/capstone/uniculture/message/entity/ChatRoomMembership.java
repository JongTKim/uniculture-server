package com.capstone.uniculture.message.entity;

import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomMembership {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "chatRoom_id")
  private ChatRoom chatRoom;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  public ChatRoomMembership(ChatRoom chatRoom, Member member) {
    this.chatRoom = chatRoom;
    this.member = member;
  }
}
