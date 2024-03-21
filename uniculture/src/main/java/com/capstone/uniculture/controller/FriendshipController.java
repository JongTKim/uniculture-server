package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Friend.DetailFriendResponseDto;
import com.capstone.uniculture.dto.Friend.FriendDto;
import com.capstone.uniculture.dto.Friend.FriendResponseDto;
import com.capstone.uniculture.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity friendRequest(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return friendService.friendRequest(memberId, friendDto.getTargetId());
    }

    /**
     * 친구 신청 취소 API
     * @request : TargetId(취소하려는 상대방의 ID, ReceiverId)
     * @response : 성공여부
     * 로직 : Sender, Receiver 가지고 FriendRequest 를 찾아서 삭제
     */
    @DeleteMapping("/auth/friend")
    public ResponseEntity revokeFriendRequest(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.revokeFriendRequest(memberId, friendDto.getTargetId()));
    }

    /**
     * 친구 삭제 API
     * @request : TargetId(삭제하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : FriendshipRepository 에서 두 MemberID를 가지고 요소를 찾아서 삭제 (쌍방으로 2개 삭제필요)
     */
    @DeleteMapping("/auth/friend/deleteFriend")
    public ResponseEntity deleteFriend(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.deleteFriend(memberId, friendDto.getTargetId()));
    }
    /**
     * 친구 요청 수락 API
     * @request : TargetId(친구신청 수락하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : Sender, Receiver 를 찾아서 서로 Friendship 맺어주고 FriendRequest 는 삭제
     */
    @PostMapping("/auth/friend/accept")
    public ResponseEntity acceptFriendRequest(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.acceptFriendRequest(memberId, friendDto.getTargetId()));
    }

    /**
     * 친구 요청 거절 API
     * @request : TargetId(친구신청 수락하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : Sender, Receiver 가지고 FriendRequest 를 찾아서 삭제
     */
    @PostMapping("/auth/friend/reject")
    public ResponseEntity rejectFriendRequest(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.rejectFriendRequest(memberId, friendDto.getTargetId()));
    }

    /**
     * 친구 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : 현재 회원의 친구목록을 가져와서 각 친구마다 프로필로 만들어서 반환
     */
    @GetMapping("/auth/friend")
    public ResponseEntity<List<FriendResponseDto>> checkFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriends(memberId));
    }

    @GetMapping("/auth/friend/detail")
    public ResponseEntity<Page<DetailFriendResponseDto>> detailFriendsList(
            @PageableDefault(size=10, direction = Sort.Direction.DESC) Pageable pageable)
    {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriends2(memberId, pageable));
    }
    /**
     * 친구 요청 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : 현재 회원의 친구 요청 목록을 가져와서 각 친구마다 프로필로 만들어서 반환
     */
    @GetMapping("/auth/friend/checkRequest")
    public ResponseEntity<List<FriendResponseDto>> checkRequestFriendsList(){
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
    public ResponseEntity<List<FriendResponseDto>> checkMyRequestFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfMyFriendRequest(memberId));
    }
}
