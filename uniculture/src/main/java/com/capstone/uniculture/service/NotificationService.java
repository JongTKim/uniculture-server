package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Notification.NotificationResponseDto;
import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDto> getNotificationList() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<Notification> notifications = notificationRepository.findAllByUserId(memberId);
        return notifications.stream().map(NotificationResponseDto::fromNotification).toList();
    }


    public Long getNotificationCount() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return notificationRepository.findAllCountByUserId(memberId);
    }

    public String checkNotification(Long notificationId) {

        Long memberId = SecurityUtil.getCurrentMemberId();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("찾는 알림이 존재하지 않습니다"));

        if(notification.getMember().getId() != memberId){
            throw new IllegalStateException("본인 알림이 아닙니다");
        }
        notification.setIsCheck(true);

        return "알림 읽기에 성공했습니다";
    }
}
