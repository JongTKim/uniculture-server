package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Friend.Friendship;
import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT COUNT(p) FROM Friendship p WHERE p.fromMember = :member")
    Integer countByMember(@Param("member") Member member);

    @Query("SELECT DISTINCT p.toMember FROM Friendship p WHERE p.fromMember.id= :member_id")
    Page<Member> findAllByFromMember_Id(@Param("member_id") Long id, Pageable pageable);

    @Query("SELECT DISTINCT f.toMember FROM Friendship f WHERE f.fromMember.id = :member_id AND f.toMember.nickname LIKE %:nickname%")
    Page<Member> findFriendsByNickname(@Param("member_id") Long id, @Param("nickname") String nickname, Pageable pageable);

    @Query("SELECT DISTINCT f.toMember FROM Friendship f WHERE f.fromMember.id = :member_id AND f.toMember.age = :age")
    Page<Member> findFriendsByAge(@Param("member_id") Long id, @Param("age") Integer age, Pageable pageable);

    @Query("SELECT DISTINCT f.toMember FROM Friendship f WHERE f.fromMember.id = :member_id AND f.toMember.gender = :gender")
    Page<Member> findFriendsByGender(@Param("member_id") Long id, @Param("gender") Gender gender, Pageable pageable);

    @Query("SELECT f.toMember FROM Friendship f JOIN f.toMember.myHobbyList h WHERE h.hobbyName = :hobbyName AND f.fromMember.id = :member_id")
    Page<Member> findFriendsByHobbyName(@Param("member_id") Long id, @Param("hobbyName") String hobbyName, Pageable pageable);




    Friendship findByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId);

    // 두 명의 회원번호를 받아서 친구관계인지 확인
    Boolean existsByFromMember_IdAndToMember_Id(Long member1, Long member2);


}
