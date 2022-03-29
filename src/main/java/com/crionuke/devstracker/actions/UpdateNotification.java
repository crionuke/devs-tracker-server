package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.NotificationForUpdateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UpdateNotification {
    private static final Logger logger = LoggerFactory.getLogger(UpdateNotification.class);

    private final String UPDATE_SQL = "UPDATE notifications " +
            "SET n_processed = TRUE, n_updated = CURRENT_TIMESTAMP WHERE n_id = ?";

    public UpdateNotification(Connection connection, long notificationId)
            throws NotificationForUpdateNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, notificationId);
            statement.executeUpdate();
            if (statement.executeUpdate() == 0) {
                throw new NotificationForUpdateNotFoundException("Notification not found, id=" + notificationId);
            } else {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Timestamp updated = generatedKeys.getTimestamp("n_updated");
                        logger.debug("Notification processed, id={}, updated={}", notificationId, updated);
                    } else {
                        throw new InternalServerException("Generated key not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }
}
