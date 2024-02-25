package com.capstone.uniculture.message.controller;

import com.capstone.uniculture.message.dto.ChatMessageDTO;
import com.capstone.uniculture.message.dto.ChatMessageHistoryDTO;
import com.capstone.uniculture.message.entity.MessageType;
import com.capstone.uniculture.message.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
public class ChatController {
  private final ChatService chatService;
  private final SimpMessageSendingOperations messagingTemplate;   //브로커 통해서 메시지 전달

  @MessageMapping("/api/chat/message")    //메시지 보내기.
  public void message(ChatMessageDTO message) {

    ChatMessageDTO savedMessage = chatService.sendMessage(message);   //데이터베이스 먼저 저장

    if (MessageType.ENTER.equals(savedMessage.getType())) { //입장할때
      savedMessage.setMessage(savedMessage.getSender() + "님이 입장하셨습니다.");
    }
    messagingTemplate.convertAndSend("/sub/chat/room/" + savedMessage.getRoomId(), savedMessage);  //roomId로 메시지 전송
  }

  @GetMapping("/api/chat/message")
  public List<ChatMessageHistoryDTO> getChatHistory(@PathVariable Long roomId){
    return chatService.findMessageHistory(roomId);
  }


  @MessageMapping("/api/chat/room/leave")   //채팅방 나가기
  public void chatRoomLeave(ChatMessageDTO message, SimpMessageHeaderAccessor headerAccessor) throws IOException {
    message.setMessage(message.getSender() + "님이 퇴장하셨습니다.");
    /*
     * TODO 사용자 제거 코드
     */
    messagingTemplate.convertAndSend("/sub/chat/room/"+ message.getRoomId(), message);
  }

}
