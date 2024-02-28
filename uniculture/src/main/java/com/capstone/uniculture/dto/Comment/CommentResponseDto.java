package com.capstone.uniculture.dto.Comment;

import com.capstone.uniculture.entity.Post.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String commentWriterName;

    @Builder
    public CommentResponseDto(Long commentId, String content, LocalDateTime createdDate, LocalDateTime modifiedDate, String commentWriterName) {
        this.commentId = commentId;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.commentWriterName = commentWriterName;
    }


    public static CommentResponseDto fromEntity(Comment comment){
        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .commentWriterName(comment.getMember().getNickname())
                .build();
    }
}
