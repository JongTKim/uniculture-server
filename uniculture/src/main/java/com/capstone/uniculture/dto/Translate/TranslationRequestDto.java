package com.capstone.uniculture.dto.Translate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class TranslationRequestDto {
    private String text;
    private String target_lang;

    public TranslationRequestDto(String text, String target_lang) {
        this.text = text;
        this.target_lang = target_lang;
    }
}
