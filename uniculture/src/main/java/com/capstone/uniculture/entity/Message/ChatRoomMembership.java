package com.capstone.uniculture.entity.Message;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomMembership extends BaseEntity {
  // 이 엔티티로 ManyToMany 관계를 풀수있을 뿐만 아니라, Member 가 ChatRoom 에 들어간 시간을 알 수 있음.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chatRoom_id")
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  public ChatRoomMembership(ChatRoom chatRoom, Member member) {
    this.chatRoom = chatRoom;
    this.member = member;
  }
}
