package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Member.Request.LoginRequestDto;
import com.capstone.uniculture.dto.Member.Request.MailRequestDto;
import com.capstone.uniculture.service.MailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Tag(name="메일", description = "메일(Chat) 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MailController {
    private final MailService mailService;
    private int number; // 이메일 인증 숫자를 저장하는 변수

    // 인증 이메일 전송
    @PostMapping("/mailSend")
    public HashMap<String, Object> mailSend(@RequestBody MailRequestDto mail) {
        HashMap<String, Object> map = new HashMap<>();

        try {
            System.out.println(mail.getEmail());
            number = mailService.sendMail(mail.getEmail());
            String num = String.valueOf(number);

            map.put("success", Boolean.TRUE);
            map.put("number", num);
        } catch (Exception e) {
            map.put("success", Boolean.FALSE);
            map.put("error", e.getMessage());
        }

        return map;
    }

    // 인증번호 일치여부 확인
    @GetMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestParam String userNumber) {

        boolean isMatch = userNumber.equals(String.valueOf(number));

        return ResponseEntity.ok(isMatch);
    }
}
