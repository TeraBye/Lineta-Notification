package com.example.lineta_notification.listener;

import com.example.lineta_notification.entity.PostNotification;
import com.example.lineta_notification.service.PostNoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Component
public class PostNotificationListener {

    @Autowired
    private PostNoteService notificationService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "post-notifications", groupId = "notification-group")
    public void handlePostNotification(String message) throws JsonProcessingException, ExecutionException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = mapper.readValue(message, new TypeReference<>() {});

        String postId = data.get("postId");
        String content = data.get("content");
        String type = data.get("type");
        String senderUsername = data.get("senderUsername");
        List<String> allUsernames = new ArrayList<>();

        if (Objects.equals(type, "post")) {
            // thay bằng list friend
            allUsernames = List.of("leosagii", "thien001");
        } else if (Objects.equals(type, "comment") || Objects.equals(type, "like")) {
            String cmtReceiver = data.get("cmtReceiver");
            allUsernames = cmtReceiver.equals(senderUsername) ? List.of() : List.of(cmtReceiver);
        }

        messagingTemplate.convertAndSend("/topic/notifications", senderUsername);

        // Chỉ tạo noti nếu danh sách không rỗng
        if (!allUsernames.isEmpty()) {
            for (String username : allUsernames) {
                PostNotification noti = PostNotification.builder()
                        .senderUsername(senderUsername)
                        .receiverUsername(username)
                        .content(content)
                        .postId(postId)
                        .timestamp(Timestamp.now())
                        .isRead(false)
                        .build();

                notificationService.savePostNote(noti);
            }
        }
    }

}

