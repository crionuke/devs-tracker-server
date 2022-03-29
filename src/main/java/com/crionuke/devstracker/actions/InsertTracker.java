package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.Tracker;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.TrackerAlreadyAddedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class InsertTracker {
    private static final Logger logger = LoggerFactory.getLogger(InsertTracker.class);

    private final String INSERT_SQL = "INSERT INTO trackers (t_user_id, t_developer_id) VALUES(?, ?)";

    private final Tracker tracker;

    public InsertTracker(Connection connection, long userId, long developerId)
            throws TrackerAlreadyAddedException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, userId);
            statement.setLong(2, developerId);
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong("t_id");
                    Timestamp added = generatedKeys.getTimestamp("t_added");
                    Timestamp lastView = generatedKeys.getTimestamp("t_last_view");
                    tracker = new Tracker(id, added, userId, developerId, lastView);
                    logger.debug("Tracker added, {}", tracker);
                } else {
                    throw new InternalServerException("Generated key not found");
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new TrackerAlreadyAddedException("Tracker already added", e);
            } else {
                throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
            }
        }
    }

    public Tracker getTracker() {
        return tracker;
    }
}
