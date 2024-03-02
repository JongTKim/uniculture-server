package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Message.ChatMessageDTO;
import com.capstone.uniculture.dto.Message.MessageResponseDto;
import com.capstone.uniculture.entity.Message.ChatMessage;
import com.capstone.uniculture.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatController {
  private final ChatService chatService;
  private final SimpMessageSendingOperations messagingTemplate;   //브로커 통해서 메시지 전달

  /**
   * 클라이언트 메시지 전송
   * @Request : PathVariable(전송하는 방의 번호), ChatMessageDTO(메시지 타입, 멤버이름, 채팅방 번호, 내용)
   * @Reponse : void(하지만, convertAndSend 로 사실상 Message 전송)
   * 로직 : ChatMessageRepository 에 받은 메시지 저장후, 이 채팅방을 구독한 전체에게 Send
   */
  @MessageMapping("/auth/chat/{roomId}")
  public void message(WebSocketSession session,
                      @DestinationVariable Long roomId,
                      ChatMessageDTO message) {
    Long userId = (Long) session.getAttributes().get("userId");
    chatService.sendMessage(userId, message);   //데이터베이스 먼저 저장
    messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, message);  //roomId로 메시지 전송
  }

  /**
   * 채팅방 내용 가져오기
   * @Request : PathVariable(가져올 방의 번호)
   * @Response : List<MessageResponseDto> (여러개의 메시지들의 컬렉션)
   * 로직 : ChatMessageRepository 에서 roomId를 가지고 전체조회,
   * Front 에서는 DTO 의 chatMessageId 번호를 가지고 정렬하면 편할듯함
   */
  @GetMapping("/auth/chat/{roomId}")
  public List<MessageResponseDto> getChatHistory(@PathVariable Long roomId){
    return chatService.findMessageHistory(roomId);
  }

  /**
   * 입장시 "~사용자가 입장하였습니다" 전송
   */
  @MessageMapping("/auth/chat/{roomId}/enter")
  public void chatRoomEnter(WebSocketSession session,
                            @DestinationVariable Long roomId){
    Long userId = (Long) session.getAttributes().get("userId");
    MessageResponseDto message = chatService.enterChatroom(userId, roomId);
    messagingTemplate.convertAndSend("/sub/chat/room/"+ roomId, message);
  }

  /**
   * 퇴장시 "~사용자가 퇴장하였습니다" 전송
   */
  @MessageMapping("/api/chat/{roomId}/leave")   //채팅방 나가기
  public void chatRoomLeave(ChatMessageDTO message) throws IOException {
    message.setMessage(message.getSender() + "님이 퇴장하셨습니다.");
    /*
     * TODO 사용자 제거 코드
     */
    messagingTemplate.convertAndSend("/sub/chat/room/"+ message.getRoomId(), message);
  }

}
