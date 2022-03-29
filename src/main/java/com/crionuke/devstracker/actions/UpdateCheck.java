package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.exceptions.CheckForUpdateNotFoundException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UpdateCheck {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCheck.class);

    private final String UPDATE_SQL = "UPDATE checks SET c_last_check = CURRENT_TIMESTAMP WHERE c_id = ?";

    public UpdateCheck(Connection connection, long checkId)
            throws CheckForUpdateNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, checkId);
            statement.executeUpdate();
            if (statement.executeUpdate() == 0) {
                throw new CheckForUpdateNotFoundException("Check not found, id=" + checkId);
            } else {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Timestamp lastCheck = generatedKeys.getTimestamp("c_last_check");
                        logger.debug("Check updated, id={}, lastCheck={}", checkId, lastCheck);
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
