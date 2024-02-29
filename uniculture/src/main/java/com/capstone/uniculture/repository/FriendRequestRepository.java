package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Friend.FriendRequest;
import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId AND fr.receiver.id = :receiverId")
    Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Boolean existsBySender_IdAndReceiver_Id(Long senderId, Long receiverId);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId")
    List<FriendRequest> findBySenderId(Long senderId);

    @Query("SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.receiver = :memberId")
    Integer countByMember(@Param("memberId") Member memberId);
}
