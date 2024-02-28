package com.capstone.uniculture.dto.Post;


import com.capstone.uniculture.dto.Comment.CommentResponseDto;
import com.capstone.uniculture.entity.Post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PostDetailDto {

    private Long postId;
    private String title;
    private String content;
    private int viewCount;
    private String writerName;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private List<CommentResponseDto> comments;

    @Builder
    public PostDetailDto(Long postId, String title, String content, int viewCount, String writerName, LocalDateTime createDate, LocalDateTime modifiedDate, List<CommentResponseDto> comments) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.writerName = writerName;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.comments = comments;
    }

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
                .build();
    }
}
