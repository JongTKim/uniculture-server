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

    // 회원 조회(상대 프로필 조회)
    @GetMapping("/otherPage/{userId}")
    public ResponseEntity<ResponseProfileDto> otherPage(@PathVariable(name="userId") Long userId) throws IOException {
        try{
            Long memberId = SecurityUtil.getCurrentMemberId(); // 로그인된 사용자라면
            System.out.println("memberId = " + memberId);
        }
        catch (RuntimeException e){
            System.out.println("e = " + e.getMessage());
        }
        finally {
            return ResponseEntity.ok(memberService.findUser(userId));
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



    // 회원이 친구요청 거절

    // 회원의 친구목록 조회

    // 회원의 친구신청함 조회


}
