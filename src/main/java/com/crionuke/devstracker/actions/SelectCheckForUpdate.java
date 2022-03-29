package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.Check;
import com.crionuke.devstracker.actions.dto.Developer;
import com.crionuke.devstracker.exceptions.CheckForUpdateNotFoundException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SelectCheckForUpdate {
    private static final Logger logger = LoggerFactory.getLogger(SelectCheckForUpdate.class);

    private final String SELECT_SQL = "" +
            "SELECT c_id, c_added, c_developer_id, c_country, c_priority, c_last_check, d_id, d_added, d_apple_id, d_name " +
            "FROM checks INNER JOIN developers ON c_developer_id = d_id " +
            "ORDER BY c_last_check, c_priority ASC " +
            "LIMIT 1 " +
            "FOR UPDATE OF checks SKIP LOCKED";

    private final Check check;
    private final Developer developer;

    public SelectCheckForUpdate(Connection connection) throws CheckForUpdateNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Check's fields
                    long checkId = resultSet.getLong(1);
                    Timestamp checkAdded = resultSet.getTimestamp(2);
                    long checkDeveloperId = resultSet.getLong(3);
                    String checkCountry = resultSet.getString(4);
                    int priority = resultSet.getInt(5);
                    Timestamp checkLastCheck = resultSet.getTimestamp(6);
                    check = new Check(checkId, checkAdded, checkDeveloperId, checkCountry, priority, checkLastCheck);
                    // Developer's fields
                    long developerId = resultSet.getLong(7);
                    Timestamp developerAdded = resultSet.getTimestamp(8);
                    long developerAppleId = resultSet.getLong(9);
                    String developerName = resultSet.getString(10);
                    developer = new Developer(developerId, developerAdded, developerAppleId, developerName);
                    logger.debug("Check for update selected, check={}, developer={}", check, developer);
                } else {
                    throw new CheckForUpdateNotFoundException("Check for update not found");
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public Check getCheck() {
        return check;
    }

    public Developer getDeveloper() {
        return developer;
    }
}
