package com.example.lineta_notification.service.impl;

import com.example.lineta_notification.entity.PostNotification;
import com.example.lineta_notification.service.PostNoteService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class PostNoteServiceImpl implements PostNoteService {
    private final Firestore firestore;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public PostNoteServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public WriteResult savePostNote (PostNotification postNote) throws ExecutionException, InterruptedException {
        Map<String, Object> postNoteFB = new HashMap<>();
        postNoteFB.put("content", postNote.getContent());
        postNoteFB.put("postID", postNote.getPostId());
        postNoteFB.put("receiverUsername", postNote.getReceiverUsername());
        postNoteFB.put("isRead", postNote.isRead());
        postNoteFB.put("timestamp", Timestamp.now());

        DocumentReference docRef = firestore.collection("post-notification").document();
        ApiFuture<WriteResult> writeResult = docRef.set(postNoteFB);
        return writeResult.get();

    }

    @Override
    public void notifyUserRealtime(PostNotification noti) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + noti.getReceiverUsername(),
                noti
        );
    }

    @Override
    public List<PostNotification> getPostNotesByUsername(String username) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<PostNotification> notifications = new ArrayList<>();

        // Query lấy notification theo receiverUsername
        Query query = db.collection("post-notification")
                .whereEqualTo("receiverUsername", username)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5); // Lấy tối đa 5 thông báo mới nhất

        QuerySnapshot querySnapshot = query.get().get();

        for (QueryDocumentSnapshot document : querySnapshot) {
            PostNotification notification = document.toObject(PostNotification.class);
            notifications.add(notification);
        }

        return notifications;
    }

}
