package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.MemberRequestDto;
import com.capstone.uniculture.dto.MemberResponseDto;
import com.capstone.uniculture.dto.TokenDto;
import com.capstone.uniculture.entity.Member;
import com.capstone.uniculture.jwt.TokenProvider;
import com.capstone.uniculture.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JoinService joinService;
    private final AuthenticationManagerBuilder managerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberRequestDto requestDto){
        // 1. 이메일 중복체크(실패시 400 코드 반환)
        if(joinService.checkEmailUnique(requestDto.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 Email 입니다");
        }

        // 2. Dto -> Member(변환할때 Password 암호화)
        Member member = requestDto.toMember(passwordEncoder);

        // 3. MemberRepository 실제 저장하는 부분(성공시 200 코드 반환)
        joinService.signup(member);
        return ResponseEntity.ok("가입이 완료되었습니다.");
    }

    /**
     * 닉네임 중복 확인 API
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkNicknameValid(@RequestParam("nickname") String nickname) throws BadRequestException {
        if(joinService.checkNicknameUnique(nickname) == true){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id 값이 중복입니다");
        }
        return ResponseEntity.ok("사용 가능한 아이디 입니다.");
        //return ResponseEntity.status(HttpStatus.OK).body(new MemberRequestDto()); -> 이건 body에 넣어보낼때
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto requestDto){
        // 1. 비밀번호 맞는지 확인
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();

        //
        Authentication authenticate = managerBuilder.getObject().authenticate(authenticationToken);

        // 3. 토큰 반환(Body 에 실려서 간다)
        return ResponseEntity.ok(tokenProvider.generateTokenDto(authenticate));
    }
}

