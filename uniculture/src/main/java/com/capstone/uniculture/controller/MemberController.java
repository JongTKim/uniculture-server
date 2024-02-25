package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.*;
import com.capstone.uniculture.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 조회(내 프로필 조회)
    @GetMapping("/auth/myPage")
    public ResponseEntity<ResponseProfileDto> myPage() throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.findUser(memberId));
    }

    // 회원 조회(상대 프로필 조회) - 로그인, 비로그인 나눠서 데이터를 다르게 줘야한다!
    @GetMapping("/otherPage/{userId}")
    public ResponseEntity<ResponseProfileDto> otherPage(@PathVariable(name="userId") Long userId) throws IOException {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            // 여기서부터는 로그인된 사용자 사용
            System.out.println("memberId = " + memberId);
            return ResponseEntity.ok(memberService.findOtherLogin(userId, memberId));
        }
        catch (RuntimeException e) {
            System.out.println("e = " + e.getMessage());
            // 여기서 부터는 로그인 되지않은 사용자 사용
            return ResponseEntity.ok(memberService.findOtherLogout(userId));
        }
    }

    // 회원 수정 中 프로필 수정 초기화면
    @GetMapping("/auth/myPage/editProfile")
    public ResponseEntity<UpdateProfileDto> editProfileForm(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.EditUserProfile(memberId));
    }

    // 회원 수정 中 프로필 수정
    @PatchMapping("/auth/myPage/editProfile")
    public ResponseEntity editProfile(@RequestPart UpdateProfileDto updateProfileDto,
                                      @RequestPart(required = false) MultipartFile profileImg) throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.UpdateUserProfile(memberId,updateProfileDto,profileImg));
    }

    // 회원 수정 中 개인정보 수정 초기화면
    @GetMapping("/auth/myPage/editInformation")
    public ResponseEntity<ResponseProfileDto> editInformationForm() throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.EditUserInformation(memberId));
    }

    // 회원 수정 中 개인정보 수정
    @PatchMapping("/auth/myPage/editInformation")
    public ResponseEntity editInformation(@RequestBody UpdateMemberDto updateMemberDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.UpdateUserInformation(memberId,updateMemberDto));
    }

    // 회원 삭제
    @DeleteMapping("/auth/myPage")
    public ResponseEntity deleteUser(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.deleteUser(memberId));
    }


}
