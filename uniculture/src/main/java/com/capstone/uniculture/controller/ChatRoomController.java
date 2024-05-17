package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Message.*;
import com.capstone.uniculture.service.ChatRoomService;
import com.capstone.uniculture.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="채팅방", description = "채팅방(ChatRoom) 관련 API 입니다.")
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
  @Operation(summary = "자신의 모든 채팅방 조회")
  @GetMapping
  public ResponseEntity<List<ChatRoomDTO>> myRoom() {
    return ResponseEntity.ok(chatRoomService.findRoomByUserId(SecurityUtil.getCurrentMemberId()));
  }

  @Operation(summary = "채팅방 생성(추후 POST로 교체예정)")
  @GetMapping("/duo")
  public ResponseEntity<ChatRoomIdResponseDto> createDuoRoom(@RequestParam Long toId){
    Long memberId = SecurityUtil.getCurrentMemberId();
    return ResponseEntity.ok(chatRoomService.checkAndCreate(memberId, toId));
  }

  //채팅방 입장하면 나와야할 화면, 채팅방 내용도 같이 가져와야함 (한번에 처리필요)

  /**
   * 채팅방 내용 가져오기 API
   * @Request : PathVariable(가져올 방의 번호)
   * @Response : List<MessageResponseDto> (여러개의 메시지들의 컬렉션)
   * 로직 : ChatMessageRepository 에서 roomId를 가지고 전체조회,
   * Front 에서는 DTO 의 chatMessageId 번호를 가지고 정렬하면 편할듯함
   */
  @Operation(summary = "한 채팅방의 채팅 목록 조회")
  @GetMapping("/{roomId}")
  public ResponseEntity<List<MessageResponseDto>> getChatHistory(@PathVariable Long roomId){
    return ResponseEntity.ok(chatService.findMessageHistory(roomId));
  }

  /**
   * 새로운 단체 채팅방 생성 API
   * @Request : CreateChatRoomDto (채팅방의 이름과 참여자 명단이 들어감. 추후 단톡방 구현을 위해)
   * @Response : ChatRoomIdResponseDto (새로 생선된 채팅방의 ID가 들어감)
   */
  /*
  @PostMapping
  public ResponseEntity<ChatRoomIdResponseDto> createRoom(@RequestBody CreateChatRoomDTO createChatRoomDTO) {
    Long memberId = SecurityUtil.getCurrentMemberId();
    return ResponseEntity.ok(chatRoomService.createChatRoomWithMember(memberId,createChatRoomDTO.getMemberId()));
  }*/

  /**
   * 채팅방에서 인원 클릭시 참여자`들의 명단 조회 API
   * @Request : roomId (pathVariable 로 수신)
   * @Response : List<ChatRoomMemberResponseDto> 참여중인 멤버들의 기초정보가 담긴 컬렉션
   */
  /*
  @GetMapping("/{roomId}/list")
  public ResponseEntity<List<ChatRoomMemberResponseDto>> memberList(@PathVariable("roomId") Long roomId){
    return ResponseEntity.ok(chatRoomService.findAllRoomMember(roomId));
  }
   */
}
