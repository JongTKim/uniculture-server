package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.MemberRequestDto;
import com.capstone.uniculture.dto.MemberResponseDto;
import com.capstone.uniculture.dto.TokenDto;
import com.capstone.uniculture.entity.Member;
import com.capstone.uniculture.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JoinService joinService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberRequestDto requestDto){
        return ResponseEntity.ok(joinService.signup(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto requestDto){
        return ResponseEntity.ok(joinService.login(requestDto));
    }
}

