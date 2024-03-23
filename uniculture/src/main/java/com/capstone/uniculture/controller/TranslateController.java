package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Translate.TranslationRequestDto;
import com.capstone.uniculture.dto.Translate.TranslationResponseDto;
import com.capstone.uniculture.service.TranslateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name="번역", description = "번역(Translation) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TranslateController {
    private final TranslateService translateService;

    @Operation(summary = "번역 기능", description = "회원의 모국어로 변환됩니다(추후 웹사이트 언어변경 기능이 생기면 선택한 언어를 Target에 넣어주면됨")
    @PostMapping("/auth/translate")
    public ResponseEntity<TranslationResponseDto> translate(@RequestBody TranslationRequestDto translationRequestDto){

        return ResponseEntity.ok(translateService.translateText(translationRequestDto.getText(), translationRequestDto.getTarget_lang()));
    }
}
