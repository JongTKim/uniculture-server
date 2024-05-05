package com.capstone.uniculture.entity.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity @Getter @NoArgsConstructor
public class MyHobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    private String hobbyName;

    // 멤버 수정시 등록을 위해 필요한 생성자
    public MyHobby(Member member, String hobbyName) {
        this.member = member;
        this.hobbyName = hobbyName;
    }
}
