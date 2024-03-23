package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.Translate.TranslationResponseDto;
import com.capstone.uniculture.dto.Translate.TranslationServerResponseDto;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TranslateService {

    @Value("${deepl.api.key}")
    private String deeplApiKey;

    private final RestTemplate restTemplate;


    public TranslateService(RestTemplateBuilder restTemplate) {
        this.restTemplate = restTemplate.build();
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

    private String buildRequestBody(String text) {
        return "{\"text\": [\"" + text + "\"]}";
    }
}
