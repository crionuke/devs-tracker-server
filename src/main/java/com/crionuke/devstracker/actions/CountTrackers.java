package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountTrackers {
    private static final Logger logger = LoggerFactory.getLogger(CountTrackers.class);

    private final String SELECT_SQL = "SELECT COUNT(t_developer_id) AS t_count FROM trackers WHERE t_user_id = ?";

    private final long count;

    public CountTrackers(Connection connection, long userId) throws InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getLong(1);
                    logger.debug("Count {} trackers, userId={}", count, userId);
                } else {
                    count = 0;
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public long getCount() {
        return count;
    }
}

