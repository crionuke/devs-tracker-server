package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.Developer;
import com.crionuke.devstracker.actions.dto.Notification;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.NotificationForUpdateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SelectNotificationForUpdate {
    private static final Logger logger = LoggerFactory.getLogger(SelectNotificationForUpdate.class);

    private final String SELECT_SQL = "" +
            "SELECT n_id, n_added, n_developer_id, n_app_apple_id, n_app_title, n_processed, n_updated, d_id, d_added, d_apple_id, d_name " +
            "FROM notifications INNER JOIN developers ON n_developer_id = d_id " +
            "WHERE n_processed = FALSE " +
            "ORDER BY n_added ASC " +
            "LIMIT 1 " +
            "FOR UPDATE OF notifications SKIP LOCKED";

    private final Notification notification;
    private final Developer developer;

    public SelectNotificationForUpdate(Connection connection)
            throws NotificationForUpdateNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Notification's fields
                    long notificationId = resultSet.getLong(1);
                    Timestamp notificationAdded = resultSet.getTimestamp(2);
                    long notificationDeveloperId = resultSet.getLong(3);
                    long notificationAppAppleId = resultSet.getLong(4);
                    String notificationAppTitle = resultSet.getString(5);
                    boolean notificationProcessed = resultSet.getBoolean(6);
                    Timestamp notificationUpdated = resultSet.getTimestamp(7);
                    // Developer's fields
                    long developerId = resultSet.getLong(8);
                    Timestamp developerAdded = resultSet.getTimestamp(9);
                    long developerAppleId = resultSet.getLong(10);
                    String developerName = resultSet.getString(11);
                    notification = new Notification(notificationId, notificationAdded, notificationDeveloperId,
                            notificationAppAppleId, notificationAppTitle, notificationProcessed, notificationUpdated);
                    developer = new Developer(developerId, developerAdded, developerAppleId, developerName);
                    logger.debug("Notification for update selected, notification={}, developer={}", notification, developer);
                } else {
                    throw new NotificationForUpdateNotFoundException("Notification for update not found");
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public Notification getNotification() {
        return notification;
    }

    public Developer getDeveloper() {
        return developer;
    }
}
