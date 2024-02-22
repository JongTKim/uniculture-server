package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.FriendRequestDto;
import com.capstone.uniculture.dto.OtherProfileDto;
import com.capstone.uniculture.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendService friendService;

    // 회원이 친구 신청
    @PostMapping("/auth/friend/request")
    public ResponseEntity friendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.friendRequest(memberId,friendRequestDto.getTargetId());
        return ResponseEntity.ok("성공");
    }

    // 회원이 친구 신청 취소
    @DeleteMapping("/auth/friend/request")
    public ResponseEntity revokeFriendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.revokeFriendRequest(memberId, friendRequestDto.getTargetId());
        return ResponseEntity.ok("성공");
    }

    // 회원이 친구 요청 수락
    @PostMapping("/auth/friend/accept")
    public ResponseEntity acceptFriendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.acceptFriendRequest(friendRequestDto.getRequestId());
        return ResponseEntity.ok("성공");
    }

    // 회원이 친구 요청 거절
    @PostMapping("/auth/friend/reject")
    public ResponseEntity rejectFriendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.rejectFriendRequest(friendRequestDto.getRequestId());
        return ResponseEntity.ok("성공");
    }

    // 회원이 친구 목록 조회
    @GetMapping("/auth/friend/check")
    public ResponseEntity<List<OtherProfileDto>> checkFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriends(memberId));
    }

    // 회원이 친구 요청받은 목록 조회
    @GetMapping("/auth/friend/check-request")
    public ResponseEntity<List<OtherProfileDto>> checkRequestFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriendRequest(memberId));
    }
    // 회원이 친구 신청한 목록 조회
    @GetMapping("/auth/friend/check-my-request")
    public ResponseEntity<List<OtherProfileDto>> checkMyRequestFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfMyFriendRequest(memberId));
    }
}
