package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.DeveloperApp;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectDeveloperApps {
    private static final Logger logger = LoggerFactory.getLogger(SelectDeveloperApps.class);

    private final String SELECT_SQL = "SELECT a_apple_id, a_release_date, l_country, l_title " +
            "FROM apps INNER JOIN links ON a_id = l_app_id WHERE a_developer_id = ?";

    private final Map<Long, DeveloperApp> developerApps;

    public SelectDeveloperApps(Connection connection, long developerId)
            throws InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setLong(1, developerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                developerApps = new HashMap<>();
                while (resultSet.next()) {
                    long appAppleId = resultSet.getLong(1);
                    Timestamp appReleaseDate = resultSet.getTimestamp(2);
                    String linkCountry = resultSet.getString(3);
                    String linkTitle = resultSet.getString(4);
                    developerApps.putIfAbsent(appAppleId, new DeveloperApp(appAppleId, appReleaseDate));
                    developerApps.get(appAppleId).addTranslation(linkCountry, linkTitle);
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public List<DeveloperApp> getDeveloperApps() {
        return new ArrayList<>(developerApps.values());
    }
}
