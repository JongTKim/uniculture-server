package com.capstone.uniculture.entity.Post;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
// 복합키를 두는대신 유효성 규칙을 만들어 두었음 (복합키는 복잡성 증가)
@Table(uniqueConstraints=
        {@UniqueConstraint(
                columnNames={"member_id","post_id"})}
)
public class PostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public PostLike(Member member, Post post) {
        this.member = member;
        this.post = post;
    }
}
