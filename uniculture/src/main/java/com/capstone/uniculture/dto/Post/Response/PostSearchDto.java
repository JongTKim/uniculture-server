package com.capstone.uniculture.dto.Post.Response;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostStatus;
import com.capstone.uniculture.entity.Post.PostTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class PostSearchDto {

    private Long postId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private String writerName;
    private PostStatus postStatus;
    private List<String> tags;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    @Builder
    public PostSearchDto(Long postId, String title, String content, Integer viewCount, Integer commentCount, Integer likeCount, String writerName,
                         PostStatus postStatus, LocalDateTime createDate, LocalDateTime modifiedDate, List<String> tags) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.writerName = writerName;
        this.postStatus = postStatus;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.tags = tags;
    }

    public static PostSearchDto fromEntity(Post post){
        return PostSearchDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .writerName(post.getMember().getNickname())
                .postStatus(post.getPostStatus())
                .createDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .tags(post.getPostTags().stream().map(PostTag::getHashtag).toList())
                .build();
    }
}
