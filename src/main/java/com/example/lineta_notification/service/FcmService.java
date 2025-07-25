package com.example.lineta_notification.service;


import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.concurrent.ExecutionException;

public interface FcmService {
    void sendNotification(String token, String title, String body) throws InterruptedException, ExecutionException, FirebaseMessagingException;
    public void saveTokenToFirebase(String username, String token);
    public String getTokenForUser(String username) throws ExecutionException, InterruptedException;

}