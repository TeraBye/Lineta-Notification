package com.example.lineta_notification.listener;

import com.example.lineta_notification.entity.PostNotification;
import com.example.lineta_notification.service.PostNoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class PostNotificationListener {

    @Autowired
    private PostNoteService notificationService;

    @KafkaListener(topics = "post-notifications", groupId = "notification-group")
    public void handlePostNotification(String message) throws JsonProcessingException, ExecutionException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = mapper.readValue(message, new TypeReference<>() {});

        String postId = data.get("postId");
        String content = data.get("content");

        // Giả lập lấy tất cả user trong hệ thống
        List<String> allUsernames = List.of("leosagii", "thien001");
        //build noti
        for (String username : allUsernames) {
            PostNotification noti = PostNotification.builder()
                    .receiverUsername(username)
                    .content(content)
                    .postId(postId)
                    .timestamp(Timestamp.now())
                    .isRead(false)
                    .build();

            notificationService.savePostNote(noti);
            notificationService.notifyUserRealtime(noti);
        }
    }
}

