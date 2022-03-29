package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.App;
import com.crionuke.devstracker.actions.dto.InsertedApp;
import com.crionuke.devstracker.actions.dto.SearchApp;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertApps {
    private static final Logger logger = LoggerFactory.getLogger(InsertApps.class);

    private final String INSERT_SQL = "INSERT INTO apps (a_apple_id, a_release_date, a_developer_id) VALUES(?, ?, ?) ON CONFLICT DO NOTHING";

    private List<InsertedApp> insertedApps;

    public InsertApps(Connection connection, long developerId, List<SearchApp> searchApps)
            throws InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (SearchApp app : searchApps) {
                statement.setLong(1, app.getAppleId());
                statement.setTimestamp(2, app.getReleaseDate());
                statement.setLong(3, developerId);
                statement.addBatch();
            }
            int[] result = statement.executeBatch();
            insertedApps = new ArrayList<>();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                for (int index = 0; index < searchApps.size(); index++) {
                    // Affected
                    if (result[index] > 0) {
                        if (generatedKeys.next()) {
                            long appId = generatedKeys.getLong("a_id");
                            Timestamp appAdded = generatedKeys.getTimestamp("a_added");
                            long appAppleId = generatedKeys.getLong("a_apple_id");
                            Timestamp appReleaseDate = generatedKeys.getTimestamp("a_release_date");
                            App app = new App(appId, appAdded, appAppleId, appReleaseDate, developerId);
                            InsertedApp insertedApp = new InsertedApp(searchApps.get(index), app);
                            insertedApps.add(insertedApp);
                        } else {
                            throw new InternalServerException("Generated key not found");
                        }
                    }
                }
            }
            if (insertedApps.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Apps inserted, developerId={}, insertedCount={}", developerId, insertedApps.size());
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Apps inserted, developerId={}, insertedCount={}, insertedApps={}",
                            developerId, insertedApps.size(), insertedApps);
                }
            } else {
                logger.debug("No new apps inserted, developerId={}", developerId);
            }

        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public List<InsertedApp> getInsertedApps() {
        return insertedApps;
    }
}
