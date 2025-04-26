package com.example.lineta_notification.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import com.google.cloud.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class PostNotification {
    private String receiverUsername;
    private String content;
    private String postId;
    private Timestamp timestamp;
    private boolean isRead = false;
}
