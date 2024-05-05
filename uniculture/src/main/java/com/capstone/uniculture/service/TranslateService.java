package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Translate.TranslationResponseDto;
import com.capstone.uniculture.dto.Translate.TranslationServerResponseDto;
import com.capstone.uniculture.repository.MemberRepository;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class TranslateService {

    @Value("${deepl.api.key}")
    private String deeplApiKey;
    private final RestTemplate restTemplate;
    private final MemberService memberService;

    @Autowired
    public TranslateService(RestTemplateBuilder restTemplate, MemberService memberService) {
        this.restTemplate = restTemplate.build();
        this.memberService = memberService;
    }

    public String translateText2(String text, String targetLanguage) {

        String deeplUrl = "https://api-free.deepl.com/v2/translate";

        System.out.println("text = " + text);

        Translator translator1 = new Translator(deeplApiKey);
        try {
            TextResult textResult = translator1.translateText("Hello", "EN", "JA");
            System.out.println(textResult.getText());
            return textResult.getText();
        } catch (DeepLException e) {
            throw new IllegalArgumentException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public TranslationResponseDto translateText(String text, String targetLanguage) {

        if(targetLanguage == null){ // 만약 번역기를 사용하는게 아니고 게시물 번역, 채팅번역이라서 Target 언어가 안왔다면
            targetLanguage = memberService.findMyLanguage(SecurityUtil.getCurrentMemberId());
        }

        System.out.println("targetLanguage = " + targetLanguage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "auth_key=" + deeplApiKey + "&text=" + text + "&target_lang=" + targetLanguage;

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TranslationServerResponseDto> responseEntity = restTemplate.exchange("https://api-free.deepl.com/v2/translate", HttpMethod.POST, requestEntity, TranslationServerResponseDto.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            TranslationServerResponseDto.TranslationDto translationDto = responseEntity.getBody().getTranslations().get(0);
            return new TranslationResponseDto(translationDto.getText(), translationDto.getDetected_source_language());

        }
        else{
            throw new RuntimeException("번역 서버 오류입니다");
        }
    }

}
