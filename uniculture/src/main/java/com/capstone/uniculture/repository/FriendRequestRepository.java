package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Friend.FriendRequest;
import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    /**
     * SENDER 와 RECEIVER 의 ID 를 받아서 친구요청 검색
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId AND fr.receiver.id = :receiverId")
    Optional<FriendRequest> findBySenderIdAndReceiverId(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    /**
     * SENDER 와 RECEIVER 의 ID 를 받아서 친구요청 검색 => 친구요청 객체는 필요없고 존재 여부 확인만 필요한 경우
     */
    Boolean existsBySender_IdAndReceiver_Id(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    /**
     * 보낸사람 ID 로 검색이 필요할 경우 => 자신이 보낸 요청들을 확인(Member 에는 받은 요청 필드만 존재하므로 보낸 요청 확인시 필요)
     */
    @Query("SELECT fr.receiver FROM FriendRequest fr WHERE fr.sender.id = :senderId")
    List<Member> findBySenderId(@Param("senderId") Long senderId);

    @Query("SELECT fr.sender FROM FriendRequest fr WHERE fr.receiver.id = :receiverId")
    List<Member> findByReceiverId(@Param("receiverId") Long receiverId);

    @Query("SELECT fr.receiver.id FROM FriendRequest fr WHERE fr.sender.id = :senderId")
    List<Long> findReceiverIdBySenderId(@Param("senderId") Long senderId);

    @Query("SELECT fr.sender.id FROM FriendRequest fr WHERE fr.receiver.id = :receiverId")
    List<Long> findSenderIdByReceiverId(@Param("receiverId") Long receiverId);


    /**
     * MemberId 를 받아 받은 요청이 몇개인지 검색 => 프록시 객체 변환없이 조회가능
     */
    @Query("SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.receiver = :memberId")
    Integer countByMember(@Param("memberId") Member memberId);
}
