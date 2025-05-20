package com.example.lineta_notification.controller;

import com.example.lineta_notification.dto.response.ApiResponse;
import com.example.lineta_notification.entity.PostNotification;
import com.example.lineta_notification.service.PostNoteService;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/post-notification")
public class PostNoteController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final PostNoteService postNoteService;

    public PostNoteController(PostNoteService postNoteService) {
        this.postNoteService = postNoteService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody PostNotification postNote) {
        try {
            WriteResult result = postNoteService.savePostNote(postNote);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(1000)
                    .message("PostNotification created at: " + result.getUpdateTime())
                    .build());
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(ApiResponse.<Void>builder()
                    .code(1001)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }



}
