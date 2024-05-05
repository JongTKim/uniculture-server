package com.capstone.uniculture.dto.Notification;

import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.entity.Notification.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponseDto {

    private Long id;
    private NotificationType notificationType;
    private String nickname;
    private String content;
    private Long relatedNum;

    @Builder
    public NotificationResponseDto(Long id, NotificationType notificationType, String nickname, String content, Long relatedNum) {
        this.id = id;
        this.notificationType = notificationType;
        this.nickname = nickname;
        this.content = content;
        this.relatedNum = relatedNum;
    }

    public static NotificationResponseDto fromNotification(Notification notification){
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .notificationType(notification.getNotificationType())
                .nickname(notification.getMember().getNickname())
                .content(notification.getContent())
                .relatedNum(notification.getRelatedNum())
                .build();
    }
}
