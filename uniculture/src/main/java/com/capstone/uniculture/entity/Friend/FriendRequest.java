package com.capstone.uniculture.entity.Friend;

import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public FriendRequest(Member sender, Member receiver, RequestStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        // 친구 신청 받는쪽의 컬렉션에 Request 를 추가해줘야함
        receiver.getReceivedRequests().add(this);
    }
}
