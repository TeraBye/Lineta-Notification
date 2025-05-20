package com.example.lineta_notification.controller;

import com.example.lineta_notification.client.UserClient;
import com.example.lineta_notification.dto.response.ApiResponse;
import com.example.lineta_notification.dto.response.NotificationDTO;
import com.example.lineta_notification.dto.response.UserDTO;
import com.example.lineta_notification.entity.PostNotification;
import com.example.lineta_notification.service.PostNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class GetNotificationController {

    private final PostNoteService postNoteService;
    private final UserClient userClient;

    public GetNotificationController(PostNoteService postNoteService, UserClient userClient) {
        this.postNoteService = postNoteService;
        this.userClient = userClient;
    }

    // API để lấy thông báo của user
    @GetMapping("/get/{username}")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotificationsByUsername(
            @PathVariable String username
    ) throws ExecutionException, InterruptedException {


        List<PostNotification> postNotes = postNoteService.getPostNotesByUsername(username);

        Set<String> usernames = postNotes.stream()
                .map(PostNotification::getSenderUsername)
                .collect(Collectors.toSet());

        Map<String, UserDTO> userMap = userClient.getUsersByUsernames(usernames);

        List<NotificationDTO> postDTOs = postNotes.stream()
                .map(post -> new NotificationDTO(post, userMap.get(post.getSenderUsername())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<NotificationDTO>>builder()
                .code(1000)
                .message("Fetched notifications successfully")
                .result(postDTOs)
                .build());
    }


    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header; // fallback
    }
}
