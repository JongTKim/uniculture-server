package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Friend.FriendDto;
import com.capstone.uniculture.dto.Friend.FriendRequestStateUpdateDto;
import com.capstone.uniculture.dto.Friend.SimpleFriendResponseDto;
import com.capstone.uniculture.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="친구요청", description = "친구요청(FriendRequests) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FriendRequestsController {

    private final FriendService friendService;

    /**
     * 친구 신청 API
     * @request : TargetId(신청하려는 상대방의 ID, ReceiverId)
     * @response : 성공여부
     * 로직 : Sender, Receiver 찾아서 FriendRequest 생성
     */

    @Operation(summary = "친구 신청")
    @PostMapping("/auth/friend-requests")
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
    @Operation(summary = "친구 신청 취소")
    @DeleteMapping("/auth/friend-requests")
    public ResponseEntity revokeFriendRequest(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.revokeFriendRequest(memberId, friendDto.getTargetId()));
    }

    @Operation(summary = "친구 요청 수락/거절")
    @PutMapping("/auth/friend-requests/{requestId}")
    public ResponseEntity updateFriendRequestStatus(
            @PathVariable Long requestId,
            @RequestBody FriendRequestStateUpdateDto stateUpdateDto){
        return ResponseEntity.ok(friendService.updateFriendRequestStatus(requestId, stateUpdateDto.getStatus()));
    }


    /**
     * 친구 요청 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : 현재 회원의 친구 요청 목록을 가져와서 각 친구마다 프로필로 만들어서 반환
     */
    @Operation(summary = "나한테 온 친구 요청 목록")
    @GetMapping("/auth/friend-requests/receive")
    public ResponseEntity<List<SimpleFriendResponseDto>> checkRequestFriendsList(){
        return ResponseEntity.ok(friendService.findFriendRequestForMe());
    }

    /**
     * 친구 신청 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : FriendRequestRepository 에서 현재 회원이 Sender 로 들어가있는 List 를 가져와 각 친구마다 프로필로 만들어서 반환
     */
    @Operation(summary = "내가 신청한 친구 신청 목록")
    @GetMapping("/auth/friend-requests/sent")
    public ResponseEntity<List<SimpleFriendResponseDto>> checkMyRequestFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.findFriendRequestFromMe(memberId));
    }


    /**
     * 친구 요청 수락 API
     * @request : TargetId(친구신청 수락하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : Sender, Receiver 를 찾아서 서로 Friendship 맺어주고 FriendRequest 는 삭제
     **/
   /* @Operation(summary = "친구 요청 수락")
    @PostMapping("/auth/friend/accept")
    public ResponseEntity acceptFriendRequest(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.acceptFriendRequest(memberId, friendDto.getTargetId()));
    }*/

    /**
     * 친구 요청 거절 API
     * @request : TargetId(친구신청 수락하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : Sender, Receiver 가지고 FriendRequest 를 찾아서 삭제
     **/
    /*@Operation(summary = "친구 요청 거절")
    @PostMapping("/auth/friend/reject")
    public ResponseEntity rejectFriendRequest(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.rejectFriendRequest(memberId, friendDto.getTargetId()));
    }*/
}
