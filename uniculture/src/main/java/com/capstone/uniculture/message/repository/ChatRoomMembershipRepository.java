package com.capstone.uniculture.message.repository;

import com.capstone.uniculture.message.entity.ChatRoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMembershipRepository extends JpaRepository<ChatRoomMembership, Long> {
  List<ChatRoomMembership> findByMember_Id(Long memberId);
  int countByChatRoom_Id(Long chatRoomId);
}
