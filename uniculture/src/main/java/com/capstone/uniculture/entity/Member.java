package com.capstone.uniculture.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data // ** 일단 Setter 까지 넣어두고 추후 뺄지 결정
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "member")
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    // 프사는 가입할때 받지 않을꺼임 -> 기본 프로필 사진 생성 필요
    private String profileUrl;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private String introduce;

    // 나이는 가입할때 받지 않을꺼임
    private Integer age;

    // 성별은 가입할때 받지 않을꺼임
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 관리자 계정과 일반 계정 관리를 위해 권한 필요
    @Enumerated(EnumType.STRING)
    private Authority authority;

    // CascadeType.ALL => 모든 연관관계들은 Member 가 변경되면 다같이 변경된다
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Post> post = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MyHobby> myHobbyList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MyLanguage> myLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<WantLanguage> wantLanguages = new ArrayList<>();

    // ** 친구관계, 지금은 MtM로 구현했지만 나중에 중간테이블 만들어 줄 필요 있음
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friend",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Member> friends = new ArrayList<>();

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Builder
    public Member(Long id, String email, String nickname, String password, Authority authority) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.authority = authority;
    }
}
