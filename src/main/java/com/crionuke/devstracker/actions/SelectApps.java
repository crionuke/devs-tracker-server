package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.App;
import com.crionuke.devstracker.actions.dto.SearchApp;
import com.crionuke.devstracker.actions.dto.SelectedApp;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectApps {
    private static final Logger logger = LoggerFactory.getLogger(SelectApps.class);

    private final String SELECT_SQL = "SELECT a_id, a_added, a_apple_id, a_release_date, a_developer_id " +
            "FROM apps WHERE a_apple_id = ANY(?)";

    private final List<SelectedApp> selectedApps;

    public SelectApps(Connection connection, List<SearchApp> searchApps) throws InternalServerException {
        Map<Long, SearchApp> registry = searchApps.stream()
                .collect(Collectors.toMap(SearchApp::getAppleId, app -> app));
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setArray(1, connection.createArrayOf("bigint",
                    searchApps.stream().map(a -> a.getAppleId()).collect(Collectors.toList()).toArray()));
            selectedApps = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long id = resultSet.getLong(1);
                    Timestamp added = resultSet.getTimestamp(2);
                    long appleId = resultSet.getLong(3);
                    Timestamp releaseDate = resultSet.getTimestamp(4);
                    long developerId = resultSet.getLong(5);
                    App app = new App(id, added, appleId, releaseDate, developerId);
                    SearchApp searchApp = registry.get(appleId);
                    selectedApps.add(new SelectedApp(searchApp, app));
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public List<SelectedApp> getSelectedApps() {
        return selectedApps;
    }
}
