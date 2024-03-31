package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    // Email 주소로 Member 찾는 로직
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    // Email 이 이미 존재하는지 판별 로직
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // 전체 멤버중 내 친구가 아닌 멤버 찾기
    @Query("SELECT m FROM Member m WHERE m.id NOT IN (SELECT f.toMember.id FROM Friendship f WHERE f.fromMember.id = :myId)")
    List<Member> findNonFriendMembers(@Param("myId") Long myId);
}
