package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Member.ResponseProfileDto;
import com.capstone.uniculture.dto.Member.UpdateMemberDto;
import com.capstone.uniculture.dto.Member.UpdateProfileDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name="회원", description = "회원(Member) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 조회(내 프로필 조회)
    @Operation(summary = "내 프로필 조회")
    @GetMapping("/auth/member/myPage")
    public ResponseEntity<ResponseProfileDto> myPage() throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.findUser(memberId));
    }

    // 회원 조회(상대 프로필 조회) - 로그인, 비로그인 나눠서 데이터를 다르게 줘야한다!
    @Operation(summary = "상대 프로필 조회")
    @GetMapping("/member/otherPage/{nickname}")
    public ResponseEntity<ResponseProfileDto> otherPage(@PathVariable(name="nickname") String nickname) throws IOException {
        try {

            Long memberId = SecurityUtil.getCurrentMemberId();
            // 여기서부터는 로그인된 사용자 사용
            System.out.println("memberId = " + memberId);

            Member findMember = memberService.findMemberByNickname(nickname);

            if(findMember.getId() == memberId){ // 자기 프로필을 조회하는경우
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(memberService.findUser(memberId));
            }
            else{
                return ResponseEntity.ok(memberService.findOtherLogin(findMember.getId(), memberId));
            }
        }
        catch (RuntimeException e) {
            System.out.println("e = " + e.getMessage());
            // 여기서 부터는 로그인 되지않은 사용자 사용
            return ResponseEntity.ok(memberService.findOtherLogout(nickname));
        }
    }

    // 회원 수정 中 프로필 수정 초기화면
    @Operation(summary = "내 프로필 수정 초기화면", description = "프로필 수정 페이지에 접속하면 현재 프로필을 보여줍니다.")
    @GetMapping("/auth/member/editProfile")
    public ResponseEntity<UpdateProfileDto> editProfileForm(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.EditUserProfile(memberId));
    }

    // 회원 수정 中 프로필 수정
    @Operation(summary = "내 프로필 수정")
    @PatchMapping("/auth/member/editProfile")
    public ResponseEntity editProfile(@RequestBody UpdateProfileDto updateProfileDto) throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.UpdateUserProfile(memberId,updateProfileDto));
    }

    // 회원 수정 中 개인정보 수정 초기화면
    @Operation(summary = "내 개인정보 수정 초기화면", description = "개인정보 수정 페이지에 접속하면 현재 개인정보를 보여줍니다.")
    @GetMapping("/auth/member/editInformation")
    public ResponseEntity<UpdateMemberDto> editInformationForm() throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.EditUserInformation(memberId));
    }

    @Operation(summary = "(미완 - 사용X) 프로필 이미지 번경")
    @PatchMapping(path={"/auth/member/editProfileImage"}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity editProfileImage(@RequestPart MultipartFile profileImg) throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.UpdateImage(memberId,profileImg));
    }
    // 회원 수정 中 개인정보 수정
    @Operation(summary = "내 개인정보 수정")
    @PatchMapping("/auth/member/editInformation")
    public ResponseEntity editInformation(@RequestBody UpdateMemberDto updateMemberDto){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.UpdateUserInformation(memberId,updateMemberDto));
    }

    // 회원 삭제
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/auth/member")
    public ResponseEntity deleteUser(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(memberService.deleteUser(memberId));
    }


}
