package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Friend.Friendship;
import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long>, JpaSpecificationExecutor<Friendship> {

    @Query("SELECT COUNT(p) FROM Friendship p WHERE p.fromMember = :member")
    Integer countByMember(@Param("member") Member member);

    /* 밑에 메소드와 통합
    @Query("SELECT DISTINCT p.toMember FROM Friendship p WHERE p.fromMember.id= :member_id")
    Page<Member> findAllByFromMember_Id_Paging(@Param("member_id") Long id, Pageable pageable);
     */
    @Query("SELECT DISTINCT p.toMember FROM Friendship p " +
            "WHERE p.fromMember.id= :member_id and (:nickname is null or p.toMember.nickname LIKE %:nickname%) ")
    List<Member> findAllByFromMember_Id(@Param("member_id") Long id, @Param("nickname") String nickname, Pageable pageable);

    /**
     * 내 친구들의 아이디 목록을 가져오는 쿼리
     */
    @Query("SELECT f.toMember.id FROM Friendship f WHERE f.fromMember.id = :member_id")
    List<Long> findMyFriendsId(@Param("member_id") Long id);

    List<Member> findAllToMemberByFromMember_Id(@Param("member_id") Long id);



    /**
     * 최소 나이 ~ 최대 나이로 검색
     */
    @Query("SELECT DISTINCT f.toMember FROM Friendship f WHERE f.fromMember.id = :member_id AND f.toMember.age BETWEEN :min_age AND :max_age")
    Page<Member> findFriendsByAge(@Param("member_id") Long id, @Param("min_age") Integer min_age, @Param("max_age") Integer max_age, Pageable pageable);


    /**
     * 성별로 검색
     */
    @Query("SELECT DISTINCT f.toMember FROM Friendship f WHERE f.fromMember.id = :member_id AND f.toMember.gender = :gender")
    Page<Member> findFriendsByGender(@Param("member_id") Long id, @Param("gender") Gender gender, Pageable pageable);

    /**
     * 취미 이름으로 검색
     */
    @Query("SELECT DISTINCT f.toMember FROM Friendship f JOIN f.toMember.myHobbyList mh WHERE f.fromMember.id=:member_id AND mh.hobbyName = :hobbyName")
    Page<Member> findFriendsByHobbyName(@Param("member_id") Long id, @Param("hobbyName") String hobbyName, Pageable pageable);

    @Query("SELECT DISTINCT f.toMember FROM Friendship f JOIN f.toMember.myLanguages ml WHERE f.fromMember.id=:member_id AND ml.language = :language")
    Page<Member> findFriendsByMyLanguage(@Param("member_id") Long id, @Param("language") String language, Pageable pageable);

    @Query("SELECT DISTINCT f.toMember FROM Friendship f JOIN f.toMember.wantLanguages wl WHERE f.fromMember.id=:member_id AND wl.language = :language")
    Page<Member> findFriendsByWantLanguage(@Param("member_id") Long id, @Param("language") String language, Pageable pageable);


    Friendship findByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId);

    // 두 명의 회원번호를 받아서 친구관계인지 확인
    Boolean existsByFromMember_IdAndToMember_Id(Long member1, Long member2);


}
