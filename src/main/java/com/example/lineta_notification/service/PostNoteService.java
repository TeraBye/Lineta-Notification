package com.example.lineta_notification.service;

import com.example.lineta_notification.entity.PostNotification;
import com.google.cloud.firestore.WriteResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PostNoteService {
    WriteResult savePostNote(PostNotification postNote) throws ExecutionException, InterruptedException;
    void notifyUserRealtime(PostNotification noti);
    List<PostNotification> getPostNotesByUsername(String username) throws ExecutionException, InterruptedException;
}
