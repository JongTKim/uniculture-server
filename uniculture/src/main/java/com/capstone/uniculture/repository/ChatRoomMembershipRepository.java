package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
import com.capstone.uniculture.entity.Message.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMembershipRepository extends JpaRepository<ChatRoomMembership, Long> {

    @Query("SELECT crm FROM ChatRoomMembership crm JOIN FETCH crm.chatRoom WHERE crm.member.id = :memberId")
  List<ChatRoomMembership> findByMember_Id(@Param("memberId") Long memberId);

  List<ChatRoomMembership> findByChatRoom_Id(Long chatroomId);

  Optional<ChatRoomMembership> findByChatRoomAndMember(ChatRoom chatRoom, Member member);


  // 멤버 아이디와 채팅방 아이디를 주고 존재하는지 확인
  Boolean existsByChatRoom_IdAndMember_Id(Long chatroomId, Long memberId);

}
