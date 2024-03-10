package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Member.MemberRequestDto;
import com.capstone.uniculture.dto.TokenDto;
import com.capstone.uniculture.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;


    // 로그인 상태 확인
    @GetMapping("/auth/sec/home")
    public ResponseEntity signOk(){
        return ResponseEntity.ok().build();
    }


    // 회원가입
    @PostMapping("/sec/signup")
    public ResponseEntity signup(@RequestBody MemberRequestDto requestDto){

        if(memberService.checkEmailDuplicate(requestDto.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("실패했습니다");
        }
        return ResponseEntity.ok(memberService.signup(requestDto));
    }

    // 닉네임 중복 확인
    @GetMapping("/sec/check")
    public ResponseEntity checkNicknameValid(@RequestParam("nickname") String nickname) throws BadRequestException {
        System.out.println("nickname = " + nickname);
        if(memberService.checkNicknameUnique(nickname)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("닉네임이 중복되었습니다.");
        }
        return ResponseEntity.ok("사용 가능한 아이디 입니다.");
    }

    // 로그인
    @PostMapping("/sec/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto requestDto){
        System.out.println("requestDto = " + requestDto);
        return ResponseEntity.ok(memberService.login(requestDto));
    }

    // 로그아웃
    @GetMapping("/auth/sec/logout")
    public ResponseEntity logout(){
        return ResponseEntity.ok(memberService.logout());
    }


}

