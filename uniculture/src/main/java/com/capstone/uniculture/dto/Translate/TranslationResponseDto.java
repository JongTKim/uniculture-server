package com.capstone.uniculture.dto.Translate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TranslationResponseDto {
    private String text;
    private String detected_source_language;

    public TranslationResponseDto(String text, String detected_source_language) {
        this.text = text;
        this.detected_source_language = detected_source_language;
    }
}
