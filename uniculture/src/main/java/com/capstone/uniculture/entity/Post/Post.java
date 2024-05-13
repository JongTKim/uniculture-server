package com.capstone.uniculture.entity.Post;

import com.capstone.uniculture.dto.Post.Request.PostUpdateDto;
import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여기에 DAILY, HELP, 언어교류, 취미 등등이 들어감
    @Enumerated(EnumType.STRING)
    private PostType posttype;

    // 여기에 NORMAL, STUDY 가 들어감
    @Enumerated(EnumType.STRING)
    private PostCategory postCategory;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    private int likeCount;

    // 관리 잘해야함 주의 - 동시성문제
    private int commentCount;

    private int viewCount;

    private String title;

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<PostTag> postTags = new ArrayList<>();

    // 게시물의 주인 표시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer_id",nullable = false)
    @BatchSize(size = 10)
    private Member member;

    public Post(Member member, String title, String content) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    // 연관관계 메소드

    public void addComment(){
        //comment.setPost(this);
        this.commentCount += 1;
    }

    public void setMember(Member newMember){
        this.member = newMember;
        newMember.getPost().add(this);
    }


    public void removeComment(){
        this.commentCount -= 1;
    }

    // 편의 메소드
    public void upViewCount(){ this.viewCount++; }

    public void likePost(){
        this.likeCount += 1;
    }

    public void unlikePost(){
        this.likeCount -= 1;
    }

    public void update(PostUpdateDto postUpdateDto) {
        this.title = postUpdateDto.getTitle();
        this.content = postUpdateDto.getContents();
        this.posttype = postUpdateDto.getPosttype();

    }
}
