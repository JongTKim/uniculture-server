package com.capstone.uniculture.entity.Message;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String nickname;

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
  private List<ChatRoomMembership> memberships = new ArrayList<>();

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
  private List<ChatMessage> messages = new ArrayList<>();

  public ChatRoom(String name, List<Member> members){
    this.name = uniqueName(name, members);
    this.nickname = members.stream()
            .map(Member::getNickname)
            .collect(Collectors.joining(", "));

  }

  //채팅방의 이름을 구별할 수 있도록 사용자들의 첫번째 이름과 방이 생성된 날짜로 구분.
  private String uniqueName(String baseName, List<Member> members) {
    String memberInitials = members.stream()
            .map(member -> member.getNickname().substring(0, 1)).collect(Collectors.joining());
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    return baseName + "_" + memberInitials + "_" + timestamp;
  }

  // 연관관계 편의 메소드
  public void addMember(Member member){
    ChatRoomMembership membership = new ChatRoomMembership(this, member);
    this.memberships.add(membership);
    memberships.add(membership);
  }

}
