package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Message.*;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
import com.capstone.uniculture.repository.ChatMessageRepository;
import com.capstone.uniculture.repository.ChatRoomMembershipRepository;
import com.capstone.uniculture.repository.ChatRoomRepository;
import com.capstone.uniculture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {
  private final MemberRepository memberRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomMembershipRepository chatRoomMembershipRepository;
  private final ChatMessageRepository chatMessageRepository;


  // Member 검색 메소드
  private Member findMember(Long memberId) {
      return memberRepository.findById(memberId)
              .orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
  }

  // Member 프록시 검색 메소드
  private Member findMemberReference(Long memberId){
      return memberRepository.getReferenceById(memberId);
  }

  // 채팅방 검색 메소드
  private ChatRoom findChatRoom(Long roomId) {
      return chatRoomRepository.findById(roomId)
              .orElseThrow(() -> new IllegalArgumentException("찾는 채팅방이 존재하지 않습니다"));
  }


  // 사용자 속한 채팅방만 조회 (기본 채팅방 목록)
  public List<ChatRoomDTO> findRoomByUserId(Long userId) {

      List<ChatRoomDTO> chatRoomDTOList = new ArrayList<>();

      // 1. chatRoomMembershipRepository 에서 유저가 속한 채팅방 검색
      List<ChatRoom> chatRoomList = chatRoomRepository.findByMember1_Id(userId);

      for (ChatRoom chatRoom : chatRoomList) {
          System.out.println("chatRoom = " + chatRoom.getId());
          ChatRoomDTO build = ChatRoomDTO.builder().id(chatRoom.getId())
                  .username(chatRoom.getMember2().getNickname())
                  .latestMessage(chatRoom.getLatestMessage())
                  .messageType(chatRoom.getMessageType())
                  .latestMessageTime(chatRoom.getLatestMessageTime())
                  .unreadCount(chatMessageRepository.countUnreadMessage(chatRoom.getId(), userId))
                  .gender(chatRoom.getMember2().getGender())
                  .age(chatRoom.getMember2().getAge())
                  .profileurl(chatRoom.getMember2().getProfileUrl())
                  .country(chatRoom.getMember2().getCountry())
                  .build();
          chatRoomDTOList.add(build);
      }

      List<ChatRoom> chatRoomList2 = chatRoomRepository.findByMember2_Id(userId);

      for (ChatRoom chatRoom : chatRoomList2) {
          System.out.println("chatRoom = " + chatRoom.getId());
          ChatRoomDTO build = ChatRoomDTO.builder().id(chatRoom.getId())
                  .username(chatRoom.getMember1().getNickname())
                  .latestMessage(chatRoom.getLatestMessage())
                  .messageType(chatRoom.getMessageType())
                  .latestMessageTime(chatRoom.getLatestMessageTime())
                  .unreadCount(chatMessageRepository.countUnreadMessage(chatRoom.getId(), userId))
                  .gender(chatRoom.getMember2().getGender())
                  .age(chatRoom.getMember2().getAge())
                  .profileurl(chatRoom.getMember2().getProfileUrl())
                  .country(chatRoom.getMember2().getCountry())
                  .build();
          chatRoomDTOList.add(build);
      }

      return chatRoomDTOList;
  }

  // 사용자 담아서 생성
  public ChatRoomIdResponseDto createChatRoomWithMember(Long memberId1, Long memberId2) {

      // 1. 생성될 멤버 2명찾기
      Member member = memberRepository.findById(memberId1).get();

      Member referenceById = memberRepository.getReferenceById(memberId1);

      Member member1 = findMemberReference(memberId1);
      Member member2 = findMemberReference(memberId2);

      // 2. 채팅룸 생성
      ChatRoom chatRoom = new ChatRoom(member1,member2);

      // 3. ID를 뽑아내기전 DB에 저장을 하여 id 자동 생성을 유도함
      chatRoomRepository.save(chatRoom);

      // 4. 만들어진 ID 반환, 이유는 채팅방에 입장했을때 ID 값이 필요하기 때문
      return new ChatRoomIdResponseDto(chatRoom.getId());
  }

    public List<ChatRoomMemberResponseDto> findAllRoomMember(Long roomId) {

      // 1. 방을 찾아서 현재 사용자가 그 채팅방에 참여상태인지 확인(보안)
        Long memberId = SecurityUtil.getCurrentMemberId();
        if(!chatRoomMembershipRepository.existsByChatRoom_IdAndMember_Id(roomId, memberId)){
            throw new IllegalArgumentException("사용자가 채팅방에 참여하지 않은 상태입니다");
        }

        // 2. 방에서 참여자 멤버를 전부찾아서 Entity -> DTO 로 변경 후 리턴
        List<ChatRoomMembership> memberList = chatRoomMembershipRepository.findByChatRoom_Id(roomId);

        return memberList.stream()
                .map(chatRoomMembership ->
                        ChatRoomMemberResponseDto.fromEntity(chatRoomMembership.getMember()))
                .collect(Collectors.toList());
    }


    public ChatRoomIdResponseDto checkAndCreate(Long memberId1, Long memberId2) {

      // 1. 한쪽 존재확인
      Optional<Long> exist = chatRoomRepository.findByMember1_IdAndMember2_Id(memberId1, memberId2);
      if(exist.isPresent()) return new ChatRoomIdResponseDto(exist.get());

      // 2. 반대쪽 존재확인
      Optional<Long> exist2 = chatRoomRepository.findByMember1_IdAndMember2_Id(memberId2, memberId1);
      if(exist2.isPresent()) return new ChatRoomIdResponseDto(exist2.get());

      // 3. 여기까지 온거면 둘 사이의 채팅방이 없다는 거니깐 만들어주자
      return createChatRoomWithMember(memberId1, memberId2);
    }
}
