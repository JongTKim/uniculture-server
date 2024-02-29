package com.capstone.uniculture.message.entity;

import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
public class ChatRoom {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String nickname;

  @OneToMany(mappedBy = "chatRoom")
  private List<ChatRoomMembership> memberships = new ArrayList<>();

  @OneToMany(mappedBy = "chatRoom")
  private List<ChatMessage> messages = new ArrayList<>();

  public ChatRoom(String name, List<Member> members){
    this.name = uniqueName(name, members);
    this.nickname = members.stream()
            .map(Member::getNickname)
            .collect(Collectors.joining(", "));

  }

  private String uniqueName(String baseName, List<Member> members) {      //채팅방의 이름을 구별할 수 있도록 사용자들의 첫번째 이름과 방이 생성된 날짜로 구분.
    String memberInitials = members.stream()
            .map(member -> member.getNickname().substring(0, 1)).collect(Collectors.joining());
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    return baseName + "_" + memberInitials + "_" + timestamp;
  }

  public void addMember(Member member){
    ChatRoomMembership membership = new ChatRoomMembership(this, member);
    memberships.add(membership);
  }
}
