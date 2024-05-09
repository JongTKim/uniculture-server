package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.MyHobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyHobbyRepository extends JpaRepository<MyHobby, Long> {


    @Query("SELECT m.hobbyName FROM MyHobby m WHERE m.member.id = :memberId")
    List<String> findAllByMemberId(Long memberId);

    @Query("SELECT m.hobbyName FROM MyHobby m WHERE m.member in :members")
    List<MyHobby> findAllByMember(@Param("members") List<Member> members);

    @Modifying
    @Query("DELETE FROM MyHobby m WHERE m.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

}
