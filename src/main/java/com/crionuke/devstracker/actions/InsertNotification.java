package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.Notification;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.NotificationAlreadyAddedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class InsertNotification {
    private static final Logger logger = LoggerFactory.getLogger(InsertNotification.class);

    private final String INSERT_SQL = "INSERT INTO notifications (n_developer_id, n_app_apple_id, n_app_title) " +
            "VALUES(?, ?, ?)";

    private final Notification notification;

    public InsertNotification(Connection connection, long developerId, long appAppleId, String appTitle)
            throws NotificationAlreadyAddedException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, developerId);
            statement.setLong(2, appAppleId);
            statement.setString(3, appTitle);
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong("n_id");
                    Timestamp added = generatedKeys.getTimestamp("n_added");
                    boolean processed = generatedKeys.getBoolean("n_processed");
                    Timestamp updated = generatedKeys.getTimestamp("n_updated");
                    notification = new Notification(id, added, developerId, appAppleId, appTitle, processed, updated);
                    logger.debug("Notification added, {}", notification);
                } else {
                    throw new InternalServerException("Generated key not found");
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new NotificationAlreadyAddedException("Notification already added, developerId=" +
                        developerId + ", appTitle=\"" + appTitle + "\"", e);
            } else {
                throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
            }
        }
    }

    public Notification getNotification() {
        return notification;
    }
}
