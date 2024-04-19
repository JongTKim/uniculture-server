package com.capstone.uniculture.dto.Comment;

import com.capstone.uniculture.entity.Post.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    private Long parentId;
    private String content;

    @Builder
    public CommentDto(String content) {
        this.content = content;
    }

    public static Comment toComment(CommentDto commentDto){
        return Comment.builder()
                .content(commentDto.getContent())
                .build();
    }
}
