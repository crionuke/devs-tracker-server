package com.crionuke.devstracker.api.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class FirebaseApi {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseApi.class);

    public FirebaseApi(@Value("${api.firebase.credentialsJson}") String credentialsJson) throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsJson.getBytes())))
                .build();
        FirebaseApp.initializeApp(options);
        logger.info("Initialized, credentialsJson=\"{}\"",
                credentialsJson.substring(0, Math.min(64, credentialsJson.length())) + "...");
    }

    public boolean subscribeDeviceToAppleDeveloper(String deviceToken, long developerAppleId)
            throws FirebaseMessagingException {
        String topic = getAppleDevelopTopic(developerAppleId);

        TopicManagementResponse response = FirebaseMessaging.getInstance()
                .subscribeToTopic(new ArrayList<String>() {{
                    add(deviceToken);
                }}, topic);
        logger.info("Subscribe device to topic, deviceToken=\"{}\", topic=\"{}\", result={}",
                "..." + deviceToken.substring(Math.max(0, deviceToken.length() - 8)),
                topic, response.getSuccessCount() == 1);
        return response.getSuccessCount() == 1;
    }

    public boolean unsubscribeDeviceFromAppleDeveloper(String deviceToken, long developerAppleId)
            throws FirebaseMessagingException {
        String topic = getAppleDevelopTopic(developerAppleId);

        TopicManagementResponse response = FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(new ArrayList<String>() {{
                    add(deviceToken);
                }}, topic);
        logger.info("Unsubscribe device from topic, deviceToken=\"{}\", topic=\"{}\", result={}",
                "..." + deviceToken.substring(Math.max(0, deviceToken.length() - 8)), topic,
                response.getSuccessCount() == 1);
        return response.getSuccessCount() == 1;
    }

    public void fire(long developerAppleId, String developerName, String appTitle) throws FirebaseMessagingException {
        String topic = getAppleDevelopTopic(developerAppleId);
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(developerName + "'s tracker")
                        .setBody("\"" + appTitle + "\" released!")
                        .build())
                .setApnsConfig(ApnsConfig.builder().setAps(Aps.builder()
                        .setContentAvailable(true)
                        // With default sound
                        .setSound("default").build()).build())
                .setTopic(topic)
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
        logger.info("Fire message to topic, topic=\"{}\", developerName=\"{}\", appTitle=\"{}\" response=\"{}\"",
                topic, developerName, appTitle, response);
    }

    public String getAppleDevelopTopic(long developerAppleId) {
        return "apple_developer_" + developerAppleId;
    }
}
