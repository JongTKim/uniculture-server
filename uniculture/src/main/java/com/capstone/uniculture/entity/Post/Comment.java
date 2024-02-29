package com.capstone.uniculture.entity.Post;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    //멤버쪽에서는 댓글을 알필요 없음, 게시물만 알고있으면됨. 따라서 단방향 관계
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private String content;

    @Builder
    public Comment(Member member, Post post, String content) {
        this.member = member;
        this.post = post;
        this.content = content;
    }

    // 연관관계 편의 메소드

    public void setPost(Post post){
        this.post = post;
        post.getComments().add(this);
    }

}
