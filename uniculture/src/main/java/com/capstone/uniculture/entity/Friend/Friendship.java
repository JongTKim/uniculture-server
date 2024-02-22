package com.capstone.uniculture.entity.Friend;

import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fromMember_id")
    private Member fromMember;

    @ManyToOne
    @JoinColumn(name = "toMember_id")
    private Member toMember;

    public Friendship(Member fromMember, Member toMember) {
        this.fromMember = fromMember;
        this.toMember = toMember;
    }
}
