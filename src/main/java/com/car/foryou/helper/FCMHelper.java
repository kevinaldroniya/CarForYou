package com.car.foryou.helper;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FCMHelper {
    public void sendNotification(String token, String title, String message) {
        // send notification to the user
        Message msg = Message.builder()
                .setToken(token)
                .putData("title", title)
                .putData("message", message)
                .build();
        try {
            FirebaseMessaging.getInstance().send(msg);
            System.out.println("Successfully sent message: " + msg.toString());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
