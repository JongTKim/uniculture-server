package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Member.Request.LoginRequestDto;
import com.capstone.uniculture.dto.Member.Request.SignupRequestDto;
import com.capstone.uniculture.dto.Member.Response.SignupResponseDto;
import com.capstone.uniculture.dto.TokenDto;
import com.capstone.uniculture.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="인증", description = "인증(Security) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;


    /**
     * 토큰을 받아 로그인 상태를 확인하는 클래스 (200일때 로그인 상태)
     * Status : 200 = 로그인 된 상태, 401 = 로그아웃 된 상태
     */
    @Operation(summary = "로그인 상태 확인", description = "토큰을 가지고 로그인을 여부를 확인할때 사용합니다")
    @GetMapping("/auth/sec/home")
    public ResponseEntity signOk(){
        return ResponseEntity.ok().build();
    }


    /**
     * 회원가입
     * Request : 회원 가입 정보 (이메일, 비밀번호, 닉네임, 나이 등등..)
     * Response : 새로운 멤버에게 부여된 ID값
     * Status : 200 = 성공, 409 = 이메일 중복
     */
    @Operation(summary = "회원가입", description = "회원가입 할때 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "409", description = "이메일 중복입니다")
    })
    @PostMapping("/sec/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto){

        // 만약, 이메일이 중복이라면 409 코드
        if(memberService.checkEmailDuplicate(requestDto.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        // 정상적으로 완료됐으면 200 코드
        return ResponseEntity.ok(memberService.signup(requestDto));
    }


    /**
     * 닉네임 중복 확인
     * parameter : 중복 확인할 닉네임
     * Status : 200 = 사용가능, 409 = 중복된 닉네임
     */
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인시 사용합니다.")
    @GetMapping("/sec/check")
    public ResponseEntity checkNicknameValid(@RequestParam("nickname") String nickname){
        System.out.println("nickname = " + nickname);
        if(memberService.checkNicknameUnique(nickname)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("닉네임이 중복되었습니다.");
        }
        return ResponseEntity.ok("사용 가능한 아이디 입니다.");
    }

    /**
     * 로그인
     * Request : 이메일, 비밀번호
     * Response : 토큰
     * Status : 200 = 성공, 401 = 로그인 에러
     */
    @Operation(summary = "로그인", description = "로그인시 사용합니다.")
    @PostMapping("/sec/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok(memberService.login(loginRequestDto));
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "현재는 사용하지 않습니다.")
    @GetMapping("/auth/sec/logout")
    public ResponseEntity logout(){
        return ResponseEntity.ok(memberService.logout());
    }


}

