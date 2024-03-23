package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Member.MemberRequestDto;
import com.capstone.uniculture.dto.Member.MemberResponseDto;
import com.capstone.uniculture.dto.TokenDto;
import com.capstone.uniculture.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="인증", description = "인증(Security) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;


    // 로그인 상태 확인
    @Operation(summary = "로그인 상태 확인", description = "토큰을 가지고 로그인을 여부를 확인할때 사용합니다")
    @GetMapping("/auth/sec/home")
    public ResponseEntity signOk(){
        return ResponseEntity.ok().build();
    }


    // 회원가입
    @Operation(summary = "회원가입", description = "회원가입 할때 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "409", description = "이메일 중복입니다")
    })
    @PostMapping("/sec/signup")
    public ResponseEntity signup(@RequestBody MemberRequestDto requestDto){

        if(memberService.checkEmailDuplicate(requestDto.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("실패했습니다");
        }
        return ResponseEntity.ok(memberService.signup(requestDto));
    }

    // 닉네임 중복 확인
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인시 사용합니다.")
    @GetMapping("/sec/check")
    public ResponseEntity checkNicknameValid(@RequestParam("nickname") String nickname) throws BadRequestException {
        System.out.println("nickname = " + nickname);
        if(memberService.checkNicknameUnique(nickname)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("닉네임이 중복되었습니다.");
        }
        return ResponseEntity.ok("사용 가능한 아이디 입니다.");
    }

    // 로그인
    @Operation(summary = "로그인", description = "로그인시 사용합니다.")
    @PostMapping("/sec/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto requestDto){
        System.out.println("requestDto = " + requestDto);
        return ResponseEntity.ok(memberService.login(requestDto));
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "현재는 사용하지 않습니다.")
    @GetMapping("/auth/sec/logout")
    public ResponseEntity logout(){
        return ResponseEntity.ok(memberService.logout());
    }


}

