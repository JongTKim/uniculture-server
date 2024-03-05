package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.Message.ChatMessageDTO;
import com.capstone.uniculture.dto.Message.MessageResponseDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Message.ChatMessage;
import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
import com.capstone.uniculture.entity.Message.MessageType;
import com.capstone.uniculture.repository.ChatMessageRepository;
import com.capstone.uniculture.repository.ChatRoomMembershipRepository;
import com.capstone.uniculture.repository.ChatRoomRepository;
import com.capstone.uniculture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final MemberRepository memberRepository;
  private final ChatRoomMembershipRepository chatRoomMembershipRepository;


  private ChatRoom findChatRoom(Long chatRoomId) {
    return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
  }

  private Member findMember(Long memberId) {
    return memberRepository.findById(memberId)
            .orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
  }

  //메시지 불러오기
  public List<MessageResponseDto> findMessageHistory(Long roomId){
    // 1. 채팅방 아이디를 가지고 전체 메시지를 불러온다 -> 데이터양이 많아져 성능저하시 추후 NoSQL 변경 가능성
    List<ChatMessage> messages = chatMessageRepository.findByChatRoom_Id(roomId);

    return messages.stream()
            .map(MessageResponseDto::fromEntity)
            .collect(Collectors.toList());
  }

  // 입장시 채팅방에 참여자를 추가해주고, 입장 안내를 날려주는 메소드
  public MessageResponseDto enterChatroom(Long senderId, Long roomId){

    Member member = findMember(senderId);
    ChatRoom chatRoom = findChatRoom(roomId);

    chatRoom.addMember(member);

    ChatMessage chatMessage = ChatMessage.builder()
            .type(MessageType.ENTER)
            .message(member.getNickname() + "님이 입장하셨습니다.")
            .chatRoom(chatRoom)
            .member(member)
            .build();

    chatMessageRepository.save(chatMessage);

    return MessageResponseDto.fromEntity(chatMessage);
  }

  // 퇴장시 채팅방에서 참여자를 제외시키고, 퇴장 안내를 날려주는 메소드
  public MessageResponseDto leaveChatroom(Long senderId, Long roomId){

    Member member = findMember(senderId);
    ChatRoom chatRoom = findChatRoom(roomId);

    ChatRoomMembership chatRoomMembership = chatRoomMembershipRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(
            () -> new IllegalArgumentException("채팅방에 사용자가 존재하지 않습니다")
    );
    chatRoomMembershipRepository.delete(chatRoomMembership);

    ChatMessage chatMessage = ChatMessage.builder()
            .type(MessageType.ENTER)
            .message(member.getNickname() + "님이 입장하셨습니다.")
            .chatRoom(chatRoom)
            .member(member)
            .build();

    chatMessageRepository.save(chatMessage);

    return MessageResponseDto.fromEntity(chatMessage);
  }

  // 메시지 보내기 -> 받아서 저장하기 메소드
  public MessageResponseDto sendMessage(Long senderId, ChatMessageDTO chatMessageDTO) {
    // 1. 채팅이 저장될 채팅방, 보내는 멤버 찾기
    ChatRoom chatRoom = findChatRoom(chatMessageDTO.getRoomId());
    Member member = findMember(senderId);

    // 2. DTO -> Entity 변환하여, ChatMessage 객체 생성
    // 시간은 생성될때 JPA Auditing 에 의해 자동으로 생성된다
    ChatMessage chatMessage = ChatMessageDTO.toChatMessage(chatMessageDTO);
    chatMessage.setChatRoom(chatRoom);
    chatMessage.setMember(member);

    // 3. chatMessageRepository 에 ChatMessage 객체 저장
    chatMessageRepository.save(chatMessage);    //데이터베이스에 저장
    chatRoom.addMessage(chatMessage);

    // 4. Entity -> DTO 변환하여 Return
    return MessageResponseDto.fromEntity(chatMessage);
  }

  //채팅방에서 키워드로 메시지 찾기
  public ChatMessageDTO findMessageByKeyword(String keyword) {
    return null;
  }



}
