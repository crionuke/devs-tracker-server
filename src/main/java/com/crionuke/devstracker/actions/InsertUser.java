package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.UserAlreadyAddedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class InsertUser {
    private static final Logger logger = LoggerFactory.getLogger(InsertUser.class);

    private final String INSERT_SQL = "INSERT INTO users (u_token, u_device) VALUES(?, ?)";

    private final User user;

    public InsertUser(Connection connection, String token, String device)
            throws UserAlreadyAddedException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, token);
            statement.setString(2, device);
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong("u_id");
                    Timestamp added = generatedKeys.getTimestamp("u_added");
                    user = new User(id, added, token, device);
                    logger.info("User added, user={}", user);
                } else {
                    throw new InternalServerException("Generated key not found");
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new UserAlreadyAddedException("User already added, token=" + token, e);
            } else {
                throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
            }
        }
    }

    public User getUser() {
        return user;
    }
}
