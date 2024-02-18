package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.*;
import com.capstone.uniculture.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody MemberRequestDto requestDto){
        return ResponseEntity.ok(memberService.signup(requestDto));
    }

    // 닉네임 중복 확인
    @GetMapping("/check")
    public ResponseEntity<?> checkNicknameValid(@RequestParam("nickname") String nickname) throws BadRequestException {
        if(memberService.checkNicknameUnique(nickname) == true){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok("사용 가능한 아이디 입니다.");
    }

    // 회원 조회
    @GetMapping("/myPage")
    public ResponseEntity<MyPageDto> myPage() throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.findUser(memberId));
    }
    // 회원 수정 中 프로필 수정 초기화면
    @GetMapping("/myPage/editProfile")
    public ResponseEntity<UpdateProfileDto> editProfileForm(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.EditUserProfile(memberId));
    }

    // 회원 수정 中 프로필 수정
    @PatchMapping("/myPage/editProfile")
    public ResponseEntity editProfile(@RequestPart UpdateProfileDto updateProfileDto,
                                      @RequestPart(required = false) MultipartFile profileImg) throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.UpdateUserProfile(memberId,updateProfileDto,profileImg));
    }

    // 회원 수정 中 개인정보 수정 초기화면
    @GetMapping("/myPage/editInformation")
    public ResponseEntity<MyPageDto> editInformationForm() throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.EditUserInformation(memberId));
    }

    // 회원 수정 中 개인정보 수정
    @PatchMapping("/myPage/editInformation")
    public ResponseEntity editInformation(@RequestBody UpdateMemberDto updateMemberDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.UpdateUserInformation(memberId,updateMemberDto));
    }

    // 회원 삭제
    @DeleteMapping("/myPage")
    public ResponseEntity deleteUser(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.deleteUser(memberId));
    }



    // 회원이 친구요청 거절

    // 회원의 친구목록 조회

    // 회원의 친구신청함 조회


}
