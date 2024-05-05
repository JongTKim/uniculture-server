package com.capstone.uniculture.entity.Notification;

import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long relatedNum; // 연관된 친구의 넘버나, 게시물의 넘버가 들어간다.

    private String content;

    private Boolean isCheck;

    @Builder
    public Notification(Long id, NotificationType notificationType, Member member, Long relatedNum, String content, Boolean isCheck) {
        this.id = id;
        this.notificationType = notificationType;
        this.member = member;
        this.relatedNum = relatedNum;
        this.content = content;
        this.isCheck = isCheck;
    }

    public void check() {
        this.isCheck = true;
    }
}
