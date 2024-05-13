package com.capstone.uniculture.dto.Post.Response;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostListDto {

    private Long postId;
    private String imageUrl;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private String writerName;
    private PostStatus postStatus;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    @Builder
    public PostListDto(Long postId, String title, String content, PostStatus postStatus,
                       Integer viewCount, Integer commentCount, Integer likeCount,
                       String writerName, LocalDateTime createDate, LocalDateTime modifiedDate,
                       String imageUrl) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.postStatus = postStatus;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.writerName = writerName;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.imageUrl = imageUrl;
    }

    public static PostListDto fromEntity(Post post){
        return PostListDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .imageUrl(post.getImageUrl())
                .content(post.getContent())
                .postStatus(post.getPostStatus())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .writerName(post.getMember().getNickname())
                .createDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .build();
    }
}
