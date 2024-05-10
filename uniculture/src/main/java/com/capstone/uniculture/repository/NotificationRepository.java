package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    // Member 의 닉네임과 url 이 필요하므로 Fetch Join 필요
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.member.id = :userId AND n.isCheck = false")
    Long findAllCountByUserId(Long userId);

    @Query("SELECT n FROM Notification n JOIN FETCH n.member WHERE n.member.id = :userId AND n.isCheck = false")
    List<Notification> findAllByUserId(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isCheck = true WHERE n.member.id = :memberId")
    void updateAllNotification(Long memberId);

}
