package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Friend.*;
import com.capstone.uniculture.dto.Recommend.ProfileRecommendRequestDto;
import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.service.FriendService;
import com.deepl.api.Usage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="친구", description = "친구(FriendShip) 관련 API 입니다.")
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

    @Operation(summary = "친구 신청")
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
    @Operation(summary = "친구 신청 취소")
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
    @Operation(summary = "친구 삭제")
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
    @Operation(summary = "친구 요청 수락")
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
    @Operation(summary = "친구 요청 거절")
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
    @Operation(summary = "내 친구 목록 간단 조회", description = "프로필에서 간단하게 조회할때 사용합니다.")
    @GetMapping("/auth/friend")
    public ResponseEntity<List<FriendResponseDto>> checkFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriends(memberId));
    }


    @Operation(summary = "내 친구 목록 상세 조회", description = "친구 페이지에서 자세하게 조회할때 사용합니다.")
    @GetMapping("/auth/friend/detail")
    public ResponseEntity<Page<DetailFriendResponseDto>> detailFriendsList(
            @PageableDefault(size=10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String name)
    {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfFriends2(name, memberId, pageable));
    }

    @Operation(summary = "친구추천", description = "친구 추천을 받을때 사용합니다")
    @GetMapping("/auth/friend/recommend")
    public ResponseEntity<List<RecommendFriendResponseDto>> recommendFriendsList(@PageableDefault(size=10, direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(friendService.recommendFriends(pageable));
    }


    @Operation(summary = "내 친구 검색")
    @GetMapping("/auth/friend/search")
    public ResponseEntity<Page<DetailFriendResponseDto>> friendSearch(
            @PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String cl, // 가능언어
            @RequestParam(required = false) String wl, // 원하는언어
            @RequestParam(required = false) String hb, // 취미
            @RequestParam(required = false) Integer mina, // 나이
            @RequestParam(required = false) Integer maxa, // 나이
            @RequestParam(required = false) Gender ge // 성별
        ){
        FriendSearchDto searchData = FriendSearchDto.createSearchData(cl,wl,hb,mina,maxa,ge);
        return ResponseEntity.ok(friendService.getMyFriendBySearch2(hb,cl,wl,mina,maxa,ge,pageable));
    }

    @Operation(summary = "전체 멤버중 필터 검색")
    @GetMapping("/auth/friend/search2")
    public ResponseEntity<Page<DetailFriendResponseDto>> friendSearch2(
            @PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String cl, // 가능언어
            @RequestParam(required = false) String wl, // 원하는언어
            @RequestParam(required = false) String hb, // 취미
            @RequestParam(required = false) Integer mina, // 나이
            @RequestParam(required = false) Integer maxa, // 나이
            @RequestParam(required = false) Gender ge // 성별
    ){
        return ResponseEntity.ok(friendService.getMyFriendBySearch4(hb, cl, wl, mina, maxa, ge, pageable));
    }
    /**
     * 친구 요청 목록 조회 API
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : 현재 회원의 친구 요청 목록을 가져와서 각 친구마다 프로필로 만들어서 반환
     */
    @Operation(summary = "나한테 온 친구 요청 목록")
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
    @Operation(summary = "내가 신청한 친구 신청 목록")
    @GetMapping("/auth/friend/checkMyRequest")
    public ResponseEntity<List<FriendResponseDto>> checkMyRequestFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.listOfMyFriendRequest(memberId));
    }
}
