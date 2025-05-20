package com.example.lineta_notification.dto.response;

import com.example.lineta_notification.entity.PostNotification;
import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String receiverUsername;
    private  String senderUsername;
    private String content;
    private String type; // POST hoặc COMMENT
    private String postId;
    private Timestamp timestamp;
    private boolean read;
    private String profilePicURL;

    public NotificationDTO(PostNotification postNotification, UserDTO user) {
        this.content = postNotification.getContent();
        this.timestamp = postNotification.getTimestamp();



        if (user != null) {
            this.senderUsername = user.getUsername();
            this.profilePicURL = user.getProfilePicURL();
        }
    }
}

