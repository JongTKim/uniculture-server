package com.capstone.uniculture.dto.Translate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslationServerResponseDto {

    private List<TranslationDto> translations;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    static public class TranslationDto {
        private String detected_source_language;
        private String text;
    }

}