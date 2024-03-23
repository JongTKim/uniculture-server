package com.capstone.uniculture.dto.Post;


import com.capstone.uniculture.dto.Comment.CommentResponseDto;
import com.capstone.uniculture.entity.Post.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDto {

    private Long postId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private String writerName;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    // 댓글도 함께 날라가야하므로
    private List<CommentResponseDto> comments;
    private Boolean isLogin;
    private Boolean isLike;
    private Boolean isMine;


    public static PostDetailDto fromEntity(Post post){
        return PostDetailDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .writerName(post.getMember().getNickname())
                .createDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .comments(post.getComments().stream()
                        .map(CommentResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .likeCount(post.getLikeCount())
                .build();
    }
}
