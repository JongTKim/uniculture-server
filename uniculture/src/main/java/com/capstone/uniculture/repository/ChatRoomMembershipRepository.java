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

  List<ChatRoomMembership> findByChatRoom_Id(Long chatroomId);

  Optional<ChatRoomMembership> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

  int countByChatRoom_Id(Long chatRoomId);

  // 멤버 아이디와 채팅방 아이디를 주고 존재하는지 확인
  Boolean existsByChatRoom_IdAndMember_Id(Long chatroomId, Long memberId);
}
