package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Notification.NotificationResponseDto;
import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림", description = "알림(Notification) 관련 API 입니다.")
@RestController
@RequestMapping("/api/auth/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "총 알림 목록 가져오기")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotificationList(){
        return ResponseEntity.ok(notificationService.getNotificationList());
    }

    @Operation(summary = "총 알림 개수")
    @GetMapping("/count")
    public ResponseEntity<Long> getNotificationCount(){
        return ResponseEntity.ok(notificationService.getNotificationCount());
    }

    @Operation(summary = "알림 한개 읽기")
    @PostMapping("/{id}")
    public ResponseEntity<String> checkNotification(@PathVariable("id") Long notificationId){
        return ResponseEntity.ok(notificationService.checkNotification(notificationId));
    }

    @Operation(summary = "알림 전부 읽기")
    @PostMapping("/all")
    public ResponseEntity<String> checkAllNotification(){
        return ResponseEntity.ok(notificationService.checkAllNotification());
    }
}
