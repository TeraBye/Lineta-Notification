package com.example.lineta_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String receiverUsername;
    private String content;
    private String type; // POST hoặc COMMENT
    private String postId;
    private LocalDateTime timestamp;
    private boolean read;
}

