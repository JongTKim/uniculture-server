package com.capstone.uniculture.entity.Message;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

  private String latestMessage;

  // 마지막 메시지 시간이 필요함 -> modifiedDate 로 하면 name 이 바뀔때도 변하므로 안됨
  private LocalDateTime latestMessageTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private Member owner;

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
  private List<ChatRoomMembership> memberships = new ArrayList<>();

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
  private List<ChatMessage> messages = new ArrayList<>();

  public ChatRoom(String name, List<Member> members){
    this.name = uniqueName(name, members);
  }

  @Builder
  public ChatRoom(Long id, String name, Member owner, List<ChatRoomMembership> memberships, List<ChatMessage> messages) {
    this.id = id;
    this.name = name;
    this.owner = owner;
    this.memberships = memberships;
    this.messages = messages;
  }

  // 연관관계 편의 메소드
  public void addMessage(ChatMessage message){
    messages.add(message);
    setLatestMessage(message.getMessage());
    setLatestMessageTime(message.getCreatedDate()); // 만들어진 시간 활용
  }

  public void addMember(Member member){
    ChatRoomMembership membership = new ChatRoomMembership(this, member);
    this.memberships.add(membership);
    memberships.add(membership);
  }

  // 사용자 편의 메소드
  //채팅방의 이름을 구별할 수 있도록 사용자들의 첫번째 이름과 방이 생성된 날짜로 구분.
  private String uniqueName(String baseName, List<Member> members) {
    String memberInitials = members.stream()
            .map(member -> member.getNickname().substring(0, 1)).collect(Collectors.joining());
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    return baseName + "_" + memberInitials + "_" + timestamp;
  }


}
