package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.TrackerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteTracker {
    private static final Logger logger = LoggerFactory.getLogger(DeleteTracker.class);

    private final String DELETE_SQL = "DELETE FROM trackers WHERE t_user_id = ? AND t_developer_id = ?";

    public DeleteTracker(Connection connection, long userId, long developerId)
            throws TrackerNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, userId);
            statement.setLong(2, developerId);
            if (statement.executeUpdate() == 0) {
                throw new TrackerNotFoundException("Tracker not found");
            } else {
                logger.debug("Tracker removed, userId={}, developerId={}", userId, developerId);
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }
}
