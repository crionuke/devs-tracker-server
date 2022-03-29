package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.Alias;
import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.exceptions.AliasAlreadyAddedException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.UserAlreadyAddedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class InsertAlias {
    private static final Logger logger = LoggerFactory.getLogger(InsertAlias.class);

    private final String INSERT_SQL = "INSERT INTO aliases (a_token, a_user_id) VALUES(?, ?)";

    private final Alias alias;

    public InsertAlias(Connection connection, String token, long userId)
            throws AliasAlreadyAddedException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, token);
            statement.setLong(2, userId);
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong("a_id");
                    Timestamp added = generatedKeys.getTimestamp("a_added");
                    alias = new Alias(id, added, token, userId);
                    logger.info("Alias for user added, alias={}", alias);
                } else {
                    throw new InternalServerException("Generated key not found");
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new AliasAlreadyAddedException("Alias already added, token=" + token, e);
            } else {
                throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
            }
        }
    }

    public Alias getAlias() {
        return alias;
    }
}
