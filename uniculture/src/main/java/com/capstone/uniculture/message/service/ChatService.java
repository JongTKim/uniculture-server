package com.capstone.uniculture.message.service;

import com.capstone.uniculture.message.dto.ChatMessageDTO;
import com.capstone.uniculture.message.entity.ChatMessage;
import com.capstone.uniculture.message.entity.ChatRoom;
import com.capstone.uniculture.message.repository.ChatMessageRepository;
import com.capstone.uniculture.message.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;


  //채팅방에서 키워드로 메시지 찾기
  public ChatMessageDTO findMessageByKeyword(String keyword) {
    return null;
  }

  //메시지 보내기
  public ChatMessageDTO sendMessage(ChatMessageDTO chatMessageDTO) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDTO.getRoomId())
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setChatRoom(chatRoom);
    chatMessage.setMessage(chatMessageDTO.getMessage());
    chatMessage.setSender(chatMessageDTO.getSender());
    chatMessage.setType(chatMessageDTO.getType());
    chatMessage.setTime(LocalDateTime.now());

    chatMessage = chatMessageRepository.save(chatMessage);    //데이터베이스에 저장
    return convertToChatMessageDTO(chatMessage);
  }

  public ChatMessageDTO convertToChatMessageDTO(ChatMessage chatMessage) {
    ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
    chatMessageDTO.setRoomId(chatMessage.getChatRoom().getId());
    chatMessageDTO.setMessage(chatMessage.getMessage());
    chatMessageDTO.setSender(chatMessage.getSender());
    chatMessageDTO.setType(chatMessage.getType());
    chatMessageDTO.setTime(chatMessage.getTime());
    return chatMessageDTO;
  }

}
