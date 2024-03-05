package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Message.*;
import com.capstone.uniculture.service.ChatRoomService;
import com.capstone.uniculture.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/room")
public class ChatRoomController {
  private final ChatRoomService chatRoomService;
  private final ChatService chatService;

  /**
   * 자신이 속한 모든 채팅방 조회 API
   * @Request : X
   * @Reponse : List<ChatRoomDTO>
   * 로직 : ChatRoomMembership Repository 에서 Member_id가 현재 접속유저인것을 검색
   */
  @GetMapping
  @ResponseBody
  public ResponseEntity<List<ChatRoomDTO>> myRoom() {
    return ResponseEntity.ok(chatRoomService.findRoomByUserId(SecurityUtil.getCurrentMemberId()));
  }

  /**
   * 새로운 채팅방 생성 API
   * @Request : CreateChatRoomDto (채팅방의 이름과 참여자 명단이 들어감. 추후 단톡방 구현을 위해)
   * @Response : ChatRoomIdResponseDto (새로 생선된 채팅방의 ID가 들어감)
   */
  @PostMapping
  @ResponseBody
  public ResponseEntity<ChatRoomIdResponseDto> createRoom(@RequestBody CreateChatRoomDTO createChatRoomDTO) {
    return ResponseEntity.ok(chatRoomService.createChatRoomWithMember(createChatRoomDTO));
  }

  //채팅방 입장하면 나와야할 화면, 채팅방 내용도 같이 가져와야함 (한번에 처리필요)

  /**
   * 채팅방 입장시 채팅의 목록들을 가져와야함. 또한 채팅방 기본정보 (참여자 명수, 채팅방 이름) 필요
   * @Request : roomId (pathVariable 로 수신)
   * @Response : 채팅의 내역들
   */
  @GetMapping("/{roomId}")
  public List<MessageResponseDto> getChatHistory(@PathVariable Long roomId){
    return chatService.findMessageHistory(roomId);
  }

  /**
   * 채팅방에서 인원 클릭시 참여자들의 명단 조회 API
   * @Request : roomId (pathVariable 로 수신)
   * @Response : List<ChatRoomMemberResponseDto> 참여중인 멤버들의 기초정보가 담긴 컬렉션
   */
  @GetMapping("/{roomId}/list")
  public ResponseEntity<List<ChatRoomMemberResponseDto>> memberList(@PathVariable("roomId") Long roomId){
    return ResponseEntity.ok(chatRoomService.findAllRoomMember(roomId));
  }
}
