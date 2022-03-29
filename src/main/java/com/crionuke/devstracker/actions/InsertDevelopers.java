package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.services.dto.SearchDeveloper;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertDevelopers {
    private static final Logger logger = LoggerFactory.getLogger(InsertDevelopers.class);

    private final String INSERT_SQL = "INSERT INTO developers (d_apple_id, d_name) VALUES(?, ?) ON CONFLICT DO NOTHING";

    public InsertDevelopers(Connection connection, List<SearchDeveloper> developers) throws InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            for (SearchDeveloper developer : developers) {
                statement.setLong(1, developer.getAppleId());
                statement.setString(2, developer.getName());
                statement.addBatch();
            }
            int[] result = statement.executeBatch();
            logger.debug("Developers inserted, developers={}, result={}", developers, result);
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }
}
