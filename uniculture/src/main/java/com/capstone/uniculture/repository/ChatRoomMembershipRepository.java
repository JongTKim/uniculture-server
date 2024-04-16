package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
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

  int countByChatRoom_Id(Long chatRoomId);

  // 멤버 아이디와 채팅방 아이디를 주고 존재하는지 확인
  Boolean existsByChatRoom_IdAndMember_Id(Long chatroomId, Long memberId);

  // 둘만 있는 채팅방이 있는지 확인하고 있으면 그 채팅방의 아이디를 반환
  @Query("SELECT crm1.chatRoom.id " +
          "FROM ChatRoomMembership crm1 " +
          "INNER JOIN ChatRoomMembership crm2 ON crm1.chatRoom.id = crm2.chatRoom.id " +
          "WHERE crm1.member.id = :memberId1 AND crm2.member.id = :memberId2 " +
          "GROUP BY crm1.chatRoom.id " +
          "HAVING COUNT(crm1.chatRoom.id) = 1")
  Optional<Long> findChatRoomsWithTwoMembers(Long memberId1, Long memberId2);
}
