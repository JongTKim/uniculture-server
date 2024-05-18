package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> , JpaSpecificationExecutor<Member> {


    // Email 주소로 Member 찾는 로직
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    // Email 이 이미 존재하는지 판별 로직
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // 전체 멤버중 내 친구가 아닌 멤버 찾기
    @Query("SELECT m FROM Member m WHERE m.id NOT IN (SELECT f.toMember.id FROM Friendship f WHERE f.fromMember.id = :myId)")
    List<Member> findNonFriendMembers(@Param("myId") Long myId);


    @Query("SELECT m FROM Member m " +
            "WHERE m.id NOT IN (SELECT f.toMember.id FROM Friendship f WHERE f.fromMember.id = :myId)" +
            "AND m.id != :myId ORDER BY FUNCTION('RAND')")
    List<Member> findNonFriendMemberEdit(@Param("myId") Long myId, Pageable pageable);

    /*
    @Query(value = "select m.* from member m " +
            "where m.id not in (select f.to_member_id from friendship f where f.from_member_id = :myId) " +
            "order by rand() limit 20", nativeQuery = true)
    List<Member> findNonFriendMemberEdit(@Param("myId") Long myId);
     */

    @Query("SELECT m.nickname FROM Member m WHERE m.id = :memberId")
    String findNicknameById(@Param("memberId") Long memberId);

    @Query("SELECT m.remainCount FROM Member m WHERE m.id = :memberId")
    Long countRemainCount(@Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE Member m SET m.remainCount = m.remainCount - 1 WHERE m.id = :memberId")
    void decrementRemainCount(@Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE Member m SET m.mainPurpose = :purpose WHERE m.id = :memberId")
    void updateMemberPurpose(@Param("purpose") String purpose, @Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE Member m SET m.introduce = :introduce, m.mainPurpose = :mainPurpose WHERE m.id = :memberId")
    void updateMemberInfo(@Param("introduce") String introduce,
                               @Param("mainPurpose") String mainPurpose,
                               @Param("memberId") Long memberId);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.nickname LIKE %:nickname% " +
            "AND (:memberId is null or m.id != :memberId)")
    Long countMemberByNickname(@Param("memberId") Long memberId, @Param("nickname") String nickname);


    /*
    @Query(value = "select count(*) from member m " +
            "where m.id not in (select to_member_id from friendship where from_member_id = :memberId)" +
            "and m.id not in (:memberId) " +
            "and m.nickname LIKE %:nickname% ", nativeQuery = true)
    Long countMemberByNotMyFriend(@Param("memberId") Long memberId, @Param("nickname") String nickname);

    @Query(value = "select count(*) from member m where m.id " +
            "in (select to_member_id from friendship where from_member_id = :memberId)" +
            "and m.nickname LIKE %:nickname% ", nativeQuery = true)
    Long countMemberByMyFriend(@Param("memberId") Long memberId, @Param("nickname") String nickname);

     */

    @Query("SELECT COUNT(m) FROM Member m " +
            "WHERE m.id IN (SELECT f.toMember.id FROM Friendship f WHERE f.fromMember.id = :memberId) " +
            "AND m.nickname LIKE CONCAT('%', :nickname, '%')")
    Long countMemberByMyFriend(@Param("memberId") Long memberId, @Param("nickname") String nickname);



    @Query("SELECT m FROM Member m WHERE m.nickname LIKE CONCAT('%', :nickname, '%')")
    List<Member> findAllByNickname(String nickname, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.id != :memberId AND m.nickname LIKE CONCAT('%', :nickname, '%')")
    List<Member> findAllByNicknameNotMine(Long memberId, String nickname, Pageable pageable);


    @Query("SELECT m FROM Member m " +
            "WHERE m.id NOT IN (SELECT f.toMember.id FROM Friendship f WHERE f.fromMember.id = :memberId) " +
            "AND m.id != :memberId " +
            "AND m.nickname LIKE CONCAT('%', :nickname, '%')")
    Page<Member> findAllByNicknameNotMyFriend(Long memberId, String nickname, Pageable pageable);
}
