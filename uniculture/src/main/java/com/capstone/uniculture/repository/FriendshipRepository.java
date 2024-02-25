package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Friend.Friendship;
import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT COUNT(p) FROM Friendship p WHERE p.fromMember = :member")
    Integer countByMember(@Param("member") Member member);

    // 두 명의 회원번호를 받아서 친구관계인지 확인
    Boolean existsByFromMember_IdAndToMember_Id(Long member1, Long member2);

}
