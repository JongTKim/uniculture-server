package com.capstone.uniculture.dto.Post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PostSearchDto {

    String title;
    String content;
    String writerName;

    @Builder
    public PostSearchDto(String title, String content, String writerName) {
        this.title = title;
        this.content = content;
        this.writerName = writerName;
    }

    public static PostSearchDto createSearchData(String title, String content, String writerName){
        return PostSearchDto.builder()
                .title(title)
                .content(content)
                .writerName(writerName)
                .build();
    }
}
