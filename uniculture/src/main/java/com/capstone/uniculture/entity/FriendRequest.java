package com.capstone.uniculture.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public FriendRequest(Member sender, Member receiver, RequestStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }
}
