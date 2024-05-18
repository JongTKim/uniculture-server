package com.capstone.uniculture.entity.Member;


import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Friend.FriendRequest;
import com.capstone.uniculture.entity.Friend.Friendship;
import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@Table(name = "member")
@NoArgsConstructor @AllArgsConstructor
@Builder
@DynamicInsert @BatchSize(size = 10)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    // 프사는 가입할때 받지 않을꺼임 -> 기본 프로필 사진 생성 필요
    @Column(columnDefinition = "TEXT")
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

    private LocalDate born;

    private String country;

    private String mainPurpose;

    @ColumnDefault("3")
    private Integer remainCount;

    // CascadeType.ALL => 모든 연관관계들은 Member 가 변경되면 다같이 변경된다
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Purpose> purpose = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Post> post = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<MyHobby> myHobbyList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<MyLanguage> myLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<WantLanguage> wantLanguages = new ArrayList<>();

    // 친구관계 中 신청리스트
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private Set<FriendRequest> receivedRequests = new HashSet<>();

    @OneToMany(mappedBy = "fromMember", cascade = CascadeType.ALL)
    private Set<Friendship> friendships = new HashSet<>();

    //채팅방 연결
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatRoomMembership> memberships = new ArrayList<>();


    // --------------- 생성자 ---------------

    /*public Member(){
        this.profileUrl = "resources/static/Noneprofile.jpg";
    }*/

    // --------------- 연관관계 편의 메소드 ---------------


    // --------------- 비즈니스 편의 메소드 ---------------
    public void addFriend(Member friend) {
        Friendship friendship = new Friendship(this, friend);
        this.friendships.add(friendship);
    }

    // 친구의 목록을 가져오는 편의 메소드
    public Set<Member> getFriends() {
        Set<Member> friends = new HashSet<>();
        for (Friendship friendship : friendships) {
            friends.add(friendship.getToMember());
        }
        return friends;
    }

    // 친구신청한 사람 목록을 가져오는 편의 메소드
    public List<Member> getRequestMembers() {
        return receivedRequests.stream()
                .map(receivedRequest -> receivedRequest.getSender())
                .collect(Collectors.toList());
    }

    // 내가 친구신청한 사람 목록을 가져오는 편의 메소드
    // -> 이거는 FriendRequestRepository 에서 뒤져야된다 Service 단에서 처리
}
