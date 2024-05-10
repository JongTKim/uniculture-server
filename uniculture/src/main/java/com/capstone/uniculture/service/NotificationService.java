package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Notification.NotificationResponseDto;
import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.entity.Notification.NotificationType;
import com.capstone.uniculture.repository.MemberRepository;
import com.capstone.uniculture.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public List<NotificationResponseDto> getNotificationList() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<Notification> notifications = notificationRepository.findAllByUserId(memberId);
        return notifications.stream().map(notification ->
                NotificationResponseDto.builder()
                        .id(notification.getId())
                        .notificationType(notification.getNotificationType())
                        .content(notification.getContent())
                        .relatedNum(notification.getNotificationType() == NotificationType.COMMENT
                                ? notification.getRelatedNum() : memberRepository.findNicknameById(notification.getRelatedNum()))
                        .build()
                ).toList();
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

    public String checkAllNotification() {

        Long memberId = SecurityUtil.getCurrentMemberId();
        notificationRepository.updateAllNotification(memberId);

        return "알림 전부 읽기에 성공했습니다";
    }
}
