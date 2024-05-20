package com.capstone.uniculture.dto.Post.Response;


import com.capstone.uniculture.dto.Comment.CommentResponseDto;
import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostStatus;
import com.capstone.uniculture.entity.Post.PostTag;
import com.capstone.uniculture.entity.Post.PostType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class PostDetailDto {

    private Long postId;
    private String title;
    private String content;
    private List<String> tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private String writerName;
    private PostType postType;
    private PostStatus postStatus;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    // 내가 로그인 상태인지
    private Boolean isLogin;
    // 내가 좋아요를 눌렀는지 -> 하트를 채울지말지 나타내기 위해
    private Boolean isLike;
    // 내 게시물인지 -> 수정버튼을 나타내기 위해
    private Boolean isMine;

    // -- 이미지 관련 --
    private String imageurl;
    private String profileurl;
    private String country;


    public static PostDetailDto fromEntity(Post post){
        return PostDetailDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .writerName(post.getMember().getNickname())
                .tags(post.getPostTags().stream().map(PostTag::getHashtag).toList())
                .createDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .postType(post.getPosttype())
                .postStatus(post.getPostStatus())
                .imageurl(post.getImageUrl())
                .profileurl(post.getMember().getProfileUrl())
                .country(post.getMember().getCountry())
                .build();
    }
}
