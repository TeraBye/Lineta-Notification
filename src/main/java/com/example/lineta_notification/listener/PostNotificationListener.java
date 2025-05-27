package com.example.lineta_notification.listener;

import com.example.lineta_notification.client.UserClient;
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
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class PostNotificationListener {

    @Autowired
    private PostNoteService notificationService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final UserClient userClient;

    public PostNotificationListener(UserClient userClient) {
        this.userClient = userClient;
    }

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
//            allUsernames = List.of("leosagii", "thien001");
            allUsernames = userClient.getFollowerUsernames(data.get("sender-uid"));
            System.out.println(allUsernames);
        } else if (Objects.equals(type, "comment") || Objects.equals(type, "like") || Objects.equals(type, "reply") ) {
            String cmtReceiver = data.get("cmtReceiver");
            allUsernames = cmtReceiver.equals(senderUsername) ? List.of() : List.of(cmtReceiver);
        } else if (Objects.equals(type, "follow")) {
            String followReceiver = data.get("followReceiver");
            allUsernames = followReceiver.equals(senderUsername) ? List.of() : List.of(followReceiver);
        }

        for (String username : allUsernames) {
            messagingTemplate.convertAndSend("/topic/notifications/" + username, senderUsername);
        }


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

