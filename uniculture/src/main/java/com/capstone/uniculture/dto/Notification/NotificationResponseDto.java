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
    private String content;
    private Object relatedNum;

    @Builder
    public NotificationResponseDto(Long id, NotificationType notificationType, String content, Object relatedNum) {
        this.id = id;
        this.notificationType = notificationType;
        this.content = content;
        this.relatedNum = relatedNum;
    }

    public static NotificationResponseDto fromNotification(Notification notification){
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .notificationType(notification.getNotificationType())
                .content(notification.getContent())
                .relatedNum(notification.getRelatedNum())
                .build();
    }
}
