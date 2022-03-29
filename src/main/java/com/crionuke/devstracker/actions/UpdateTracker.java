package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.TrackerForUpdateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UpdateTracker {
    private static final Logger logger = LoggerFactory.getLogger(UpdateTracker.class);

    private final String UPDATE_SQL = "UPDATE trackers SET t_last_view = CURRENT_TIMESTAMP " +
            "WHERE t_user_id = ? AND t_developer_id = ?";

    public UpdateTracker(Connection connection, long userId, long developerId)
            throws TrackerForUpdateNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, userId);
            statement.setLong(2, developerId);
            statement.executeUpdate();
            if (statement.executeUpdate() == 0) {
                throw new TrackerForUpdateNotFoundException("Tracker not found, " +
                        "userId=" + userId + ", developerId=" + developerId);
            } else {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Timestamp lastView = generatedKeys.getTimestamp("t_last_view");
                        logger.debug("Tracker updated, userId={}, developerId={}, lastView={}",
                                userId, developerId, lastView);
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
