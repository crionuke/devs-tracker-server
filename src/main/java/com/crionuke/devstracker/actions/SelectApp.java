package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.App;
import com.crionuke.devstracker.exceptions.AppNotFoundException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SelectApp {
    private static final Logger logger = LoggerFactory.getLogger(SelectApp.class);

    private final String SELECT_SQL = "SELECT a_id, a_added, a_release_date, a_developer_id FROM apps WHERE a_apple_id = ?";

    private final App app;

    public SelectApp(Connection connection, long appAppleId) throws AppNotFoundException, InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setLong(1, appAppleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long appId = resultSet.getLong(1);
                    Timestamp appAdded = resultSet.getTimestamp(2);
                    Timestamp appReleaseDate = resultSet.getTimestamp(3);
                    long appDeveloperId = resultSet.getLong(4);
                    app = new App(appId, appAdded, appAppleId, appReleaseDate, appDeveloperId);
                } else {
                    throw new AppNotFoundException("App not found, appAppleId" + appAppleId);
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public App getApp() {
        return app;
    }
}
