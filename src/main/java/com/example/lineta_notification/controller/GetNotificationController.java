package com.example.lineta_notification.controller;

import com.example.lineta_notification.entity.PostNotification;
import com.example.lineta_notification.service.PostNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*") // Cho phép frontend gọi API
public class GetNotificationController {

    private final PostNoteService postNoteService;


    public GetNotificationController(PostNoteService postNoteService) {
        this.postNoteService = postNoteService;
    }

    // API để lấy thông báo của user
    @GetMapping("/{username}")
    public List<PostNotification> getNotificationsByUsername(@PathVariable String username) throws ExecutionException, InterruptedException {
        return postNoteService.getPostNotesByUsername(username);
    }
}
