package com.capstone.uniculture.dto.Comment;

import com.capstone.uniculture.entity.Post.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
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

    // -- 이미지 관련 --
    private String profileurl;
    private String country;

    public static CommentResponseDto fromEntity(Comment comment){
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .commentWriterName(comment.getMember().getNickname())
                .isDeleted(comment.getIsDeleted())
                .profileurl(comment.getMember().getProfileUrl())
                .country(comment.getMember().getCountry())
                .build();
    }
}
