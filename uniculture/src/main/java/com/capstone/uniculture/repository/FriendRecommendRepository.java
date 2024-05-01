package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Friend.FriendRecommend;
import com.capstone.uniculture.entity.Friend.FriendRecommendPK;
import com.capstone.uniculture.entity.Post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRecommendRepository extends JpaRepository<FriendRecommend, FriendRecommendPK> {

    @Query(value = "SELECT fr.* FROM friend_recommend fr " +
            "WHERE fr.created_date >= DATEADD(DAY, -1, CURRENT_DATE) " +
            "AND fr.from_id = :memberId ", nativeQuery = true)
    List<FriendRecommend> findAlreadyRecommend(@Param("memberId") Long memberId);
}
