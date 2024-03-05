package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Message.ChatRoomIdResponseDto;
import com.capstone.uniculture.dto.Message.ChatRoomMemberResponseDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.dto.Message.ChatRoomDTO;
import com.capstone.uniculture.dto.Message.CreateChatRoomDTO;
import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
import com.capstone.uniculture.repository.ChatRoomMembershipRepository;
import com.capstone.uniculture.repository.ChatRoomRepository;
import com.capstone.uniculture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


  // Member 검색 메소드
  private Member findMember(Long memberId) {
      return memberRepository.findById(memberId)
              .orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
  }

    private ChatRoom findChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("찾는 채팅방이 존재하지 않습니다"));
    }


  // 사용자 속한 채팅방만 조회 (기본 채팅방 목록)
  public List<ChatRoomDTO> findRoomByUserId(Long userId){

      // 1. chatRoomMembershipRepository 에서 유저가 속한 채팅방 검색
      List<ChatRoomMembership> memberships = chatRoomMembershipRepository.findByMember_Id(userId);

      // 2. 채팅방 -> DTO 로 변경후 Response
      return memberships.stream()
            .map(membership ->
                    ChatRoomDTO.fromEntity(membership.getChatRoom())
            )
            .collect(Collectors.toList());
  }

  // 사용자 담아서 생성
  public ChatRoomIdResponseDto createChatRoomWithMember(CreateChatRoomDTO createChatRoomDTO) {

      // 1. JWT 토큰 정보에서 채팅방의 주인 찾기
      Member owner = findMember(SecurityUtil.getCurrentMemberId());

      // 2. 채팅룸 생성
      ChatRoom chatRoom = ChatRoom.builder()
              .name(createChatRoomDTO.getName())
              .owner(owner)
              .build();

      // 3. 채팅룸에 멤버관계 주입
      createChatRoomDTO.getMemberIds().stream()
              .map(this::findMember)
              .forEach(chatRoom::addMember);

      // 4. ID를 뽑아내기전 DB에 저장을 하여 id 자동 생성을 유도함
      chatRoomRepository.save(chatRoom);

      // 5. 만들어진 ID 반환, 이유는 채팅방에 입장했을때 ID 값이 필요하기 때문
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
}
