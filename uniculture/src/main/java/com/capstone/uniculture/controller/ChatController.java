package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Message.ChatMessageDTO;
import com.capstone.uniculture.dto.Message.MessageResponseDto;
import com.capstone.uniculture.entity.Message.ChatMessage;
import com.capstone.uniculture.jwt.TokenProvider;
import com.capstone.uniculture.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;
import java.security.Principal;
import java.util.*;


@Tag(name="채팅", description = "채팅(Chat) 관련 API 입니다.")
@RequiredArgsConstructor
@Controller
//@RequestMapping("/api")
@RestController
public class ChatController {
  private final ChatService chatService;
  private final SimpMessageSendingOperations messagingTemplate;   //브로커 통해서 메시지 전달
  private static final Map<String, Long> sessions = new HashMap<>();

  /**
   * 클라이언트 메시지 전송 WebSocket
   * @Request : PathVariable(전송하는 방의 번호), ChatMessageDTO(메시지 타입, 멤버이름, 채팅방 번호, 내용)
   * @Reponse : void(하지만, convertAndSend 로 사실상 Message 전송)
   * 로직 : ChatMessageRepository 에 받은 메시지 저장후, 이 채팅방을 구독한 전체에게 Send
   */
  @Operation(summary = "채팅 전송")
  @MessageMapping("/chat/{roomId}")
  public void message(//WebSocketSession session,
                      @DestinationVariable Long roomId,
                      @Payload ChatMessageDTO message,
                      SimpMessageHeaderAccessor accessor) {

    Long writerId = sessions.get(accessor.getSessionId());
    List<Object> objects = chatService.sendMessage2(writerId, message);
    MessageResponseDto message1 = (MessageResponseDto) objects.get(0);
    ChatMessageDTO message2 = (ChatMessageDTO) objects.get(1);

    // ChatMessageDTO message2 = chatService.sendMessage2(writerId, message);//데이터베이스 먼저 저장

    System.out.println("보낼방의 아이디는? = " + roomId);
    messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, message1);  //roomId로 메시지 전송
    System.out.println("보낼쪽의 아이디는? = " + message2.getReceiver());
    messagingTemplate.convertAndSend("/sub/chat/user/" + message2.getReceiver(), message2);  //roomId로 메시지 전송
    messagingTemplate.convertAndSend("/sub/chat/user/" + writerId, message2);  //roomId로 메시지 전송
  }

  @EventListener(SessionConnectEvent.class)
  public void onConnect(SessionConnectEvent event) {
    System.out.println("세션 연결됨");
    String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
    System.out.println("내용이 뭘까? = " + event.getMessage().getHeaders().get("nativeHeaders").toString());
    String userId = event.getMessage().getHeaders().get("nativeHeaders").toString().split("User=\\[")[1].split("]")[0];
    System.out.println("userId = " + userId);
    sessions.put(sessionId, Long.parseLong(userId));
  }

  @EventListener(SessionDisconnectEvent.class)
  public void onDisconnect(SessionDisconnectEvent event) {
    System.out.println("세션 끊김");
    sessions.remove(event.getSessionId());
  }

  @Operation(summary = "내가 안읽은 채팅 개수 가져오기 - 현재는 알림에서만 사용")
  @GetMapping("/api/auth/chat/count")
  public ResponseEntity<Long> getChatCount(){
    return ResponseEntity.ok(chatService.unreadMessageCount());
  }

  /**
   * 입장시 입장 안내문 WebSocket
   * 입장시 "~사용자가 입장하였습니다" 전송
   */
  @MessageMapping("/api/auth/chat/{roomId}/enter")
  public void chatRoomEnter(WebSocketSession session,
                            @DestinationVariable Long roomId){
    Long userId = (Long) session.getAttributes().get("userId");
    MessageResponseDto message = chatService.enterChatroom(userId, roomId);
    messagingTemplate.convertAndSend("/sub/chat/room/"+ roomId, message);
  }

  /**
   * 퇴장시 퇴장 안내문 WebSocket
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
