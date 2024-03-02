package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Message.ChatRoom;
import com.capstone.uniculture.entity.Message.ChatRoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMembershipRepository extends JpaRepository<ChatRoomMembership, Long> {

  List<ChatRoomMembership> findByMember_Id(Long memberId);

  Optional<ChatRoomMembership> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

  int countByChatRoom_Id(Long chatRoomId);
}
