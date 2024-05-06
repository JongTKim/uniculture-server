package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
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
    Long memberId = SecurityUtil.getCurrentMemberId();

    List<MessageResponseDto> collect = messages.stream()
            .map(MessageResponseDto::fromEntity)
            .collect(Collectors.toList());

    chatMessageRepository.readChatMessage(roomId,memberId);

    return collect;
  }

  // 입장시 채팅방에 참여자를 추가해주고, 입장 안내를 날려주는 메소드
  public MessageResponseDto enterChatroom(Long senderId, Long roomId){

    Member member = findMember(senderId);
    ChatRoom chatRoom = findChatRoom(roomId);


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
  public ChatMessageDTO sendMessage(Long writerId, ChatMessageDTO chatMessageDTO) {
    // 1. 채팅이 저장될 채팅방, 보내는 멤버 찾기
    ChatRoom chatRoom = findChatRoom(chatMessageDTO.getRoomId());

    System.out.println("저장할 사람 아이디는? = " + writerId);
    Member member = findMember(writerId);

    // 2. DTO -> Entity 변환하여, ChatMessage 객체 생성
    // 시간은 생성될때 JPA Auditing 에 의해 자동으로 생성된다
    ChatMessage chatMessage = ChatMessageDTO.toChatMessage(chatMessageDTO);
    chatMessage.setChatRoom(chatRoom);
    chatMessage.setMember(member);



    // 3. chatMessageRepository 에 ChatMessage 객체 저장
    chatMessageRepository.save(chatMessage);    //데이터베이스에 저장
    chatRoom.addMessage(chatMessage);

    // 4. Entity -> DTO 변환하여 Return
    chatMessageDTO.setSender(member.getNickname());
    chatMessageDTO.setReceiver(chatRoom.getMember1().getId());
    return chatMessageDTO;
  }

  public List<Object> sendMessage2(Long writerId, ChatMessageDTO chatMessageDTO) {
    // 1. 채팅이 저장될 채팅방, 보내는 멤버 찾기
    ChatRoom chatRoom = findChatRoom(chatMessageDTO.getRoomId());

    System.out.println("저장할 사람 아이디는? = " + writerId);
    Member member = findMember(writerId);

    // 2. DTO -> Entity 변환하여, ChatMessage 객체 생성
    // 시간은 생성될때 JPA Auditing 에 의해 자동으로 생성된다
    ChatMessage chatMessage = ChatMessageDTO.toChatMessage(chatMessageDTO);
    chatMessage.setChatRoom(chatRoom);
    chatMessage.setMember(member);

    if(chatRoom.getMember1().getId() == writerId) chatMessage.setToMember(chatRoom.getMember2());
    else chatMessage.setToMember(chatRoom.getMember1());

    // 3. chatMessageRepository 에 ChatMessage 객체 저장
    chatMessageRepository.save(chatMessage);    //데이터베이스에 저장

    chatRoom.addMessage(chatMessage); // update 쿼리 나가야함

    // 4. 채팅방 id 로 응답해줄 DTO
    MessageResponseDto messageResponseDto = MessageResponseDto.fromEntity(chatMessage);

    // 5. 유저 id 로 응답해줄 DTO
    chatMessageDTO.setSender(member.getNickname());
    if(chatRoom.getMember1().getId() == writerId){
      chatMessageDTO.setReceiver(chatRoom.getMember2().getId());
    }
    else{
      chatMessageDTO.setReceiver(chatRoom.getMember1().getId());
    }
    chatMessageDTO.setCreatedDate(LocalDateTime.now());

    List<Object> lists = new ArrayList<>();
    lists.add(messageResponseDto);
    lists.add(chatMessageDTO);

    return lists;
  }

  //채팅방에서 키워드로 메시지 찾기
  public ChatMessageDTO findMessageByKeyword(String keyword) {
    return null;
  }

  public Long unreadMessageCount() {
    Long memberId = SecurityUtil.getCurrentMemberId();
    return chatMessageRepository.countChatMessageByMember_Id(memberId);
  }

}
