package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Message.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long > {
}
