package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Notification.NotificationResponseDto;
import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotificationList(){
        return ResponseEntity.ok(notificationService.getNotificationList());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getNotificationCount(){
        return ResponseEntity.ok(notificationService.getNotificationCount());
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> checkNotification(@PathVariable("id") Long notificationId){
        return ResponseEntity.ok(notificationService.checkNotification(notificationId));
    }

    @PostMapping("/all")
    public ResponseEntity<String> checkAllNotification(){
        return ResponseEntity.ok(notificationService.checkAllNotification());
    }
}
