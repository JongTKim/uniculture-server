package com.capstone.uniculture.entity.Message;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity @Getter @Setter
@NoArgsConstructor
public class ChatRoom extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String latestMessage;

  @Enumerated(EnumType.STRING)
  private MessageType messageType;

  private LocalDateTime latestMessageTime;
  // 마지막 메시지 시간이 필요함 -> modifiedDate 로 하면 name 이 바뀔때도 변하므로 안됨

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="member1_id", nullable = false)
  private Member member1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="member2_id", nullable = false)
  private Member member2;

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
  private List<ChatMessage> messages = new ArrayList<>();


  public ChatRoom(String name, List<Member> members){
    this.name = uniqueName(name, members);
  }

  public ChatRoom(Member member1, Member member2) {
    this.member1 = member1;
    this.member2 = member2;
  }

  // 연관관계 편의 메소드
  public void addMessage(ChatMessage message){
    messages.add(message);
    setLatestMessage(message.getMessage());
    setLatestMessageTime(message.getCreatedDate()); // 만들어진 시간 활용
    setMessageType(message.getType());
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
