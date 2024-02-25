package com.capstone.uniculture.message.service;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.message.dto.ChatRoomDTO;
import com.capstone.uniculture.message.dto.CreateChatRoomDTO;
import com.capstone.uniculture.message.entity.ChatMessage;
import com.capstone.uniculture.message.entity.ChatRoom;
import com.capstone.uniculture.message.entity.ChatRoomMembership;
import com.capstone.uniculture.message.repository.ChatRoomMembershipRepository;
import com.capstone.uniculture.message.repository.ChatRoomRepository;
import com.capstone.uniculture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
  private final MemberRepository memberRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomMembershipRepository chatRoomMembershipRepository;

  //모든 채팅방 ( 사용 안할것)
//  public List<ChatRoomDTO> findAllRoom(){
//    List<ChatRoom> chatRooms = chatRoomRepository.findAll();
//    return chatRooms.stream().map(this::convertToChatRoomDTO).collect(Collectors.toList());
//  }

  //채팅방 아이디로 검색 ( 수정해야할듯 채팅방이름으로 )
//  public ChatRoomDTO findRoomById(String roomId){
//    return chatRoomRepository.findById(roomId)
//            .map(this::convertToChatRoomDTO)
//            .orElse(null);
//  }

//  사용자 속한 채팅방만 조회 (기본 채팅방 목록)
  public List<ChatRoomDTO> findRoomByUserId(Long userId){
    List<ChatRoomMembership> memberships = chatRoomMembershipRepository.findByMember_Id(userId);
    return memberships.stream()
            .map(membership -> {
              ChatRoom chatRoom = membership.getChatRoom();
              ChatRoomDTO chatRoomDTO = convertToChatRoomWithDetails(chatRoom);
              return chatRoomDTO;
            })
            .collect(Collectors.toList());
  }

  private ChatRoomDTO convertToChatRoomWithDetails(ChatRoom chatRoom) {
    ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
    chatRoomDTO.setId(chatRoom.getId());
    chatRoomDTO.setName(chatRoom.getName());
    chatRoomDTO.setNickname(chatRoom.getNickname());
    chatRoomDTO.setMemberCount(chatRoomMembershipRepository.countByChatRoom_Id(chatRoom.getId()));
    return null;
  }


  //채팅방 사용자로 검색

  //사용자 담아서 생성
  public CreateChatRoomDTO createChatRoomWithMember(CreateChatRoomDTO dto) {
    //먼저 채팅방 생성 후 저장
    ChatRoom chatRoom = new ChatRoom();
    chatRoom.setName(dto.getName());
    ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

    //채팅방과 사용자 연관관계 생성 후 저장
    List<ChatRoomMembership> memberships = dto.getMemberIds().stream()
            .map(memberId -> {
              Member member = memberRepository.findById(memberId).orElseThrow();
              ChatRoomMembership membership = new ChatRoomMembership();
              membership.setChatRoom(savedChatRoom);
              membership.setMember(member);
              return chatRoomMembershipRepository.save(membership);
            }).collect(Collectors.toList());

    //채팅방도 저장하고 사용자와 연결도 하고 만들어진거 반환
    CreateChatRoomDTO resDTO = new CreateChatRoomDTO();
    resDTO.setName(savedChatRoom.getName());
    resDTO.setMemberIds(memberships.stream().map(member -> member.getId()).collect(Collectors.toList()));
    return resDTO;
  }



  //ChatRoomDTO로 변환
  public ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
    ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
    chatRoomDTO.setId(chatRoom.getId());
    chatRoomDTO.setName(chatRoom.getName());
    return chatRoomDTO;
  }

}
