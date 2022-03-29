package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.Alias;
import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.exceptions.AliasNotFoundException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SelectAlias {
    private static final Logger logger = LoggerFactory.getLogger(SelectAlias.class);

    private final String SELECT_SQL = "SELECT a_id, a_added, u_id, u_added, u_token, u_device " +
            "FROM aliases JOIN users ON a_user_id = u_id WHERE a_token = ?";

    private final Alias alias;
    private final User user;

    public SelectAlias(Connection connection, String token)
            throws AliasNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long aliasId = resultSet.getLong(1);
                    Timestamp aliasAdded = resultSet.getTimestamp(2);
                    long userId = resultSet.getLong(3);
                    Timestamp userAdded = resultSet.getTimestamp(4);
                    String userToken = resultSet.getString(5);
                    String userDevice = resultSet.getString(6);
                    alias = new Alias(aliasId, aliasAdded, token, userId);
                    user = new User(userId, userAdded, userToken, userDevice);
                    logger.debug("Alias selected, alias={}, user={}", alias, user);
                } else {
                    throw new AliasNotFoundException("Alias not found, token=" + token);
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public Alias getAlias() {
        return alias;
    }

    public User getUser() {
        return user;
    }
}
