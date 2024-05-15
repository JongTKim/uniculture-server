package com.capstone.uniculture.entity.Friend;

import com.capstone.uniculture.entity.BaseEntity;
import com.capstone.uniculture.entity.Member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor @AllArgsConstructor
@DynamicUpdate
public class FriendRecommend extends BaseEntity {

    @EmbeddedId
    private FriendRecommendPK friendRecommendPK;

    @Column(nullable = false)
    private Boolean isOpen = false;

    @Column(nullable = false)
    private Long similarity;

    public FriendRecommend(Member fromMember, Member toMember, Long similarity) {
        this.friendRecommendPK = new FriendRecommendPK(fromMember, toMember);
        this.isOpen = false;
        this.similarity = similarity;
    }

    public void changeStatus(Boolean isOpen){
        this.isOpen = isOpen;
    }
}
