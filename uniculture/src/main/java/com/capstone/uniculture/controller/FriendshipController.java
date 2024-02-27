package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.FriendRequestDto;
import com.capstone.uniculture.dto.OtherProfileDto;
import com.capstone.uniculture.dto.ResponseProfileDto;
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

    /**
     * 친구 신청 API
     * @request : TargetId(신청하려는 상대방의 ID, ReceiverId)
     * @response : 성공여부
     * 로직 : Sender, Receiver 찾아서 FriendRequest 생성
     */
    @PostMapping("/auth/friend")
    public ResponseEntity friendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.friendRequest(memberId,friendRequestDto.getTargetId());
        return ResponseEntity.ok("성공");
    }

    /**
     * 친구 신청 취소 API
     * @request : TargetId(취소하려는 상대방의 ID, ReceiverId)
     * @response : 성공여부
     * 로직 : Sender, Receiver 가지고 FriendRequest 를 찾아서 삭제
     */
    @DeleteMapping("/auth/friend")
    public ResponseEntity revokeFriendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.revokeFriendRequest(memberId, friendRequestDto.getTargetId());
        return ResponseEntity.ok("성공");
    }

    /**
     * 친구 삭제 API
     * @request : TargetId(삭제하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : FriendshipRepository 에서 두 MemberID를 가지고 요소를 찾아서 삭제 (쌍방으로 2개 삭제필요)
     */
    @DeleteMapping("/auth/friend/deleteFriend")
    public ResponseEntity deleteFriend(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.deleteFriend(memberId, friendRequestDto.getTargetId());
        return ResponseEntity.ok("성공");
    }
    /**
     * 친구 수락 API
     * @request : TargetId(친구신청 수락하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : Sender, Receiver 를 찾아서 서로 Friendship 맺어주고 FriendRequest 는 삭제
     */
    @PostMapping("/auth/friend/accept")
    public ResponseEntity acceptFriendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.acceptFriendRequest(memberId,friendRequestDto.getTargetId());
        return ResponseEntity.ok("수락함");
    }

    /**
     * 친구 거절 API
     * @request : TargetId(친구신청 수락하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : Sender, Receiver 가지고 FriendRequest 를 찾아서 삭제
     */
    @PostMapping("/auth/friend/reject")
    public ResponseEntity rejectFriendRequest(@RequestBody FriendRequestDto friendRequestDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        friendService.rejectFriendRequest(memberId,friendRequestDto.getTargetId());
        return ResponseEntity.ok("거절됨");
    }

    /**
     * 친구 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : 현재 회원의 친구목록을 가져와서 각 친구마다 프로필로 만들어서 반환
     */
    @GetMapping("/auth/friend")
    public ResponseEntity<List<ResponseProfileDto>> checkFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriends(memberId));
    }

    /**
     * 친구 요청 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : 현재 회원의 친구 요청 목록을 가져와서 각 친구마다 프로필로 만들어서 반환
     */
    @GetMapping("/auth/friend/checkRequest")
    public ResponseEntity<List<ResponseProfileDto>> checkRequestFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriendRequest(memberId));
    }

    /**
     * 친구 신청 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : FriendRequestRepository 에서 현재 회원이 Sender 로 들어가있는 List 를 가져와 각 친구마다 프로필로 만들어서 반환
     */
    @GetMapping("/auth/friend/checkMyRequest")
    public ResponseEntity<List<ResponseProfileDto>> checkMyRequestFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfMyFriendRequest(memberId));
    }
}
