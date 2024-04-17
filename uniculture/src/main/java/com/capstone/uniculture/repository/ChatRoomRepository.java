package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long > {


    @Query("SELECT c.id FROM ChatRoom c WHERE c.member1.id = :member1 AND c.member2.id = :member2")
    Optional<Long> findByMember1_IdAndMember2_Id(@Param("member1") Long member1, @Param("member2") Long member2);

    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.member2 WHERE c.member1.id = :memberId")
    List<ChatRoom> findByMember1_Id(@Param("memberId") Long memberId);

    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.member1 WHERE c.member2.id = :memberId")
    List<ChatRoom> findByMember2_Id(@Param("memberId") Long memberId);
}
