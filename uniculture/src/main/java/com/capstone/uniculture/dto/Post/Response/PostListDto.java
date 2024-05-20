package com.capstone.uniculture.dto.Post.Response;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostListDto {

    private Long postId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private String writerName;
    private PostStatus postStatus;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    // -- 이미지 관련 --
    private String imageurl;
    private String profileurl;
    private String country;

    public static PostListDto fromEntity(Post post){
        return PostListDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postStatus(post.getPostStatus())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .writerName(post.getMember().getNickname())
                .createDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .imageurl(post.getImageUrl())
                .profileurl(post.getMember().getProfileUrl())
                .country(post.getMember().getCountry())
                .build();
    }
}
