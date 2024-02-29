package com.capstone.uniculture.message.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.message.dto.ChatRoomDTO;
import com.capstone.uniculture.message.dto.CreateChatRoomDTO;
import com.capstone.uniculture.message.service.ChatRoomService;
import com.capstone.uniculture.message.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/chat")
public class ChatRoomController {
  private final ChatRoomService chatRoomService;

  //채팅 리스트
  @GetMapping("/room")
  public String rooms(Model model) {
    return "/chat/room";
  }

  //모든 채팅방
  @GetMapping("/rooms")
  @ResponseBody
  public List<ChatRoomDTO> room() {
    return chatRoomService.findRoomByUserId(SecurityUtil.getCurrentMemberId());
  }

  //채팅방 생성
  @PostMapping("/room")
  @ResponseBody
  public CreateChatRoomDTO createRoom(@RequestBody CreateChatRoomDTO dto) {
    return chatRoomService.createChatRoomWithMember(dto);
  }

  //채팅방 입장 화면?
  @GetMapping("/room/enter/{roomId}")
  public String roomDetail(Model model, @PathVariable String roomId) {
    model.addAttribute("roomId", roomId);
    return "/chat/roomdetail";
  }

//  //특정 채팅방 조회
//  @GetMapping("/room/{roomId}")
//  @ResponseBody
//  public ChatRoomDTO roomInfo(@PathVariable String roomId) {
//    return chatRoomService.findRoomById(roomId);
//  }

  //사용자가 속한 채팅방 조회
//  @GetMapping("/api/rooms/my")
//  @ResponseBody
//  public List<ChatRoomDTO> myRooms(){
//    Long userId = SecurityUtil.getCurrentMemberId();
//    if(userId == null){
//      // 코드 뭔지 모르겠ㄱㄴ
//    }
//    return chatRoomService.findRoomByUserId(userId);
//  }
}
