package com.crionuke.devstracker.robots;

import com.crionuke.devstracker.actions.SelectNotificationForUpdate;
import com.crionuke.devstracker.actions.UpdateNotification;
import com.crionuke.devstracker.actions.dto.Developer;
import com.crionuke.devstracker.actions.dto.Notification;
import com.crionuke.devstracker.api.firebase.FirebaseApi;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.NotificationForUpdateNotFoundException;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class Notifier {
    private static final Logger logger = LoggerFactory.getLogger(Notifier.class);

    private final DataSource dataSource;
    private final FirebaseApi firebaseApi;

    public Notifier(DataSource dataSource, FirebaseApi firebaseApi) {
        this.dataSource = dataSource;
        this.firebaseApi = firebaseApi;
    }

    @Scheduled(fixedRate = 6000)
    public void run() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                SelectNotificationForUpdate selectNotificationForUpdate = new SelectNotificationForUpdate(connection);
                Notification notification = selectNotificationForUpdate.getNotification();
                Developer developer = selectNotificationForUpdate.getDeveloper();
                try {
                    firebaseApi.fire(developer.getAppleId(), developer.getName(), notification.getAppTitle());
                } catch (FirebaseMessagingException e) {
                    throw new InternalServerException(e.getMessage(), e);
                }
                UpdateNotification updateNotification = new UpdateNotification(connection, notification.getId());
                connection.commit();
            } catch (NotificationForUpdateNotFoundException e) {
                logger.debug(e.getMessage());
                rollbackNoException(connection);
            } catch (InternalServerException e) {
                logger.warn(e.getMessage(), e);
                rollbackNoException(connection);
            }
        } catch (SQLException e) {
            logger.warn("Datasource unavailable, " + e.getMessage(), e);
        }
    }

    private void rollbackNoException(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.warn("Rollback failed, {}", e.getMessage(), e);
        }
    }
}
