package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId AND fr.receiver.id = :receiverId")
    FriendRequest findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId")
    List<FriendRequest> findBySenderId(Long senderId);
}
