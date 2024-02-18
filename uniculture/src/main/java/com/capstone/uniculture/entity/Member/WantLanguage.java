package com.capstone.uniculture.entity.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class WantLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    private String language;

    private Integer level;


    public WantLanguage(Member member, String language, Integer level) {
        this.member = member;
        this.language = language;
        this.level = level;
    }
}
