package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.MemberRequestDto;
import com.capstone.uniculture.dto.TokenDto;
import com.capstone.uniculture.dto.UpdateMemberDto;
import com.capstone.uniculture.jwt.TokenProvider;
import com.capstone.uniculture.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final AuthenticationManagerBuilder managerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 회원가입
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

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto requestDto){
        return ResponseEntity.ok(memberService.login(requestDto));
    }

    // 로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity logout(){
        return ResponseEntity.ok(memberService.logout());
    }

    // 회원 조회

    // 프로필 변경창 선택시 현재 프로필

    // 프로필 변경

    // 개인정보 변경창 선택시 현재 개인정보

    // 개인정보 변경 중 비밀번호 변경
    @PatchMapping("/myPage/password")
    public ResponseEntity updateUser(@RequestBody UpdateMemberDto updateMemberDto, @RequestPart MultipartFile profileImg){
        Long memberId = SecurityUtil.getCurrentMemberId();
        //return ResponseEntity.ok(memberService.UpdateUserProfile(memberId,updateMemberDto,profileImg));
        return ResponseEntity.ok(null);
    }

}

