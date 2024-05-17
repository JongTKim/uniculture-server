package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Friend.*;
import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.service.FriendService;
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
     * 친구 목록 조회 API => 닉네임으로 검색가능
     * @request : X
     * @response : List<ResponseProfileDto>
     * 로직 : 현재 회원의 친구목록을 가져와서 각 친구마다 프로필로 만들어서 반환
     */
    @Operation(summary = "내 친구 목록 간단 조회", description = "프로필에서 간단하게 조회할때 사용합니다.")
    @GetMapping("/auth/friend")
    public ResponseEntity<List<SimpleFriendResponseDto>> checkFriendsList(@RequestParam(required = false) String nickname)
    {
        return ResponseEntity.ok(friendService.findMyFriendInSimple(nickname));
    }

    /**
     * 친구 삭제 API
     * @request : TargetId(삭제하려는 상대방의 ID)
     * @response : 성공여부
     * 로직 : FriendshipRepository 에서 두 MemberID를 가지고 요소를 찾아서 삭제 (쌍방으로 2개 삭제필요)
     */
    @Operation(summary = "친구 삭제")
    @DeleteMapping("/auth/friend")
    public ResponseEntity deleteFriend(@RequestBody FriendDto friendDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(friendService.deleteFriend(memberId, friendDto.getTargetId()));
    }


    @Operation(summary = "내 친구 목록 상세 조회", description = "친구 페이지에서 자세하게 조회할때 사용합니다.")
    @GetMapping("/auth/friend/detail")
    public ResponseEntity<Page<DetailFriendResponseDto>> detailFriendsList(
            @PageableDefault(size=10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String nickname)
    {
        return ResponseEntity.ok(friendService.findMyFriendInDetail(nickname, pageable));
    }

    //-------------------------------------- 친구추천 관련 로직 --------------------------------------

    @Operation(summary = "친구추천", description = "친구 추천을 받을때 사용합니다")
    @GetMapping("/auth/friend/recommend")
    public ResponseEntity<List<RecommendFriendResponseDto>> recommendFriendsList(){
        return ResponseEntity.ok(friendService.beforeRecommendFriend());
    }

    @Operation(summary = "친구추천 남은횟수", description = "매일 12시에 3회로 초기화")
    @GetMapping("/auth/friend/recommend/count")
    public ResponseEntity<Long> recommendCount(){
        return ResponseEntity.ok(friendService.recommendCountCheck());
    }

    @Operation(summary = "친구추천 새로고침", description = "다른 친구 추천을 받아보고싶을때 사용합니다. 일 횟수제한 3")
    @PostMapping("/auth/friend/recommend")
    public ResponseEntity<List<RecommendFriendResponseDto>> reRecommendFriendsList(){
        Long memberId = SecurityUtil.getCurrentMemberId();

        if(friendService.recommendCountDown(memberId)){
            return ResponseEntity.ok(friendService.recommendFriend(memberId));
        }
        else{
            throw new IllegalArgumentException("가능한 횟수가 없습니다");
        }

    }

    @Operation(summary = "친구추천 카드 오픈")
    @PostMapping("/auth/friend/recommend/open")
    public ResponseEntity<String> openProfile(@RequestBody FriendDto friendDto){
        friendService.openProfile(friendDto.getTargetId());
        return ResponseEntity.ok("성공");
    }

    /*
     * TODO Parameter -> ModelAttribute 로 정리
     */
    @Operation(summary = "내 친구 검색")
    @GetMapping("/auth/friend/search")
    public ResponseEntity<Page<DetailFriendResponseDto>> friendSearch(
            @PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cl, // 가능언어
            @RequestParam(required = false) String wl, // 원하는언어
            @RequestParam(required = false) String hb, // 취미
            @RequestParam(required = false) Integer mina, // 나이
            @RequestParam(required = false) Integer maxa, // 나이
            @RequestParam(required = false) Gender ge // 성별
        ){
        FriendSearchDto searchData = FriendSearchDto.createSearchData(cl,wl,hb,mina,maxa,ge);
        return ResponseEntity.ok(friendService.getMyFriendBySearch2(name, hb,cl,wl,mina,maxa,ge,pageable));
    }

    // 전체 멤버중 필터 검색 기능
    /*
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
    */


}
