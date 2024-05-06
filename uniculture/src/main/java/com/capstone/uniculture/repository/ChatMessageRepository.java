package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Message.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findByChatRoom_Id(Long roomId);

  @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.toMember.id = :memberId AND c.isRead = FALSE")
  Long countChatMessageByMember_Id(Long memberId);
}
