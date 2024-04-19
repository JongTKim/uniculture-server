package com.capstone.uniculture.dto.Comment;

import com.capstone.uniculture.entity.Post.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String commentWriterName;
    private Boolean isMine = Boolean.FALSE;
    private Boolean postMine = Boolean.FALSE;
    private Boolean isDeleted = Boolean.FALSE;
    private List<CommentResponseDto> children = new ArrayList<>();

    @Builder
    public CommentResponseDto(Long id, String content, LocalDateTime createdDate, LocalDateTime modifiedDate, String commentWriterName, Boolean isDeleted) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.commentWriterName = commentWriterName;
        this.isDeleted = isDeleted;
    }


    public static CommentResponseDto fromEntity(Comment comment){
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .commentWriterName(comment.getMember().getNickname())
                .isDeleted(comment.getIsDeleted())
                .build();
    }
}
