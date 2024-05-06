package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Message.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findByChatRoom_Id(Long roomId);

  @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.toMember.id = :memberId AND c.isRead = FALSE")
  Long countChatMessageByMember_Id(Long memberId);

  @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.chatRoom.id = :chatRoomId AND c.isRead = FALSE AND c.toMember.id = :memberId")
  Long countUnreadMessage(Long chatRoomId, Long memberId);

  @Modifying
  @Query("UPDATE ChatMessage c SET c.isRead = true WHERE c.chatRoom.id = :chatRoomId and c.toMember.id = :memberId")
  void readChatMessage(Long chatRoomId, Long memberId);
}
