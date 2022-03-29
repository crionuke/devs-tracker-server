package com.crionuke.devstracker.robots;

import com.crionuke.devstracker.actions.*;
import com.crionuke.devstracker.actions.dto.*;
import com.crionuke.devstracker.api.apple.AppleApi;
import com.crionuke.devstracker.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component
public class Checker {
    private static final Logger logger = LoggerFactory.getLogger(Checker.class);

    private final DataSource dataSource;
    private final AppleApi appleApi;

    public Checker(DataSource dataSource, AppleApi appleApi) {
        this.dataSource = dataSource;
        this.appleApi = appleApi;
    }

    @Scheduled(fixedRate = 6000)
    public void run() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                SelectCheckForUpdate selectCheckForUpdate = new SelectCheckForUpdate(connection);
                Check check = selectCheckForUpdate.getCheck();
                Developer developer = selectCheckForUpdate.getDeveloper();
                logger.debug("Handle check={}, developer={}", check, developer);
                // Lookup developer for apps
                List<SearchApp> searchApps = appleApi
                        .lookupDeveloper(developer.getAppleId(), check.getCountry())
                        .flatMapMany(response -> Flux.fromIterable(response.getResults()))
                        .filter(result -> result.getWrapperType().equals("software"))
                        .map(result ->
                                new SearchApp(result.getTrackId(), result.getTrackCensoredName(),
                                        result.getTrackViewUrl(), result.getReleaseDate()))
                        .collectList()
                        .block();
                logger.debug("Lookup developer, name=\"{}\", appsCount={}, searchApps={}",
                        developer.getName(), searchApps.size(), searchApps);
                InsertApps insertApps = new InsertApps(connection, developer.getId(), searchApps);
                List<InsertedApp> insertedApps = insertApps.getInsertedApps();
                for (InsertedApp insertedApp: insertedApps) {
                    if (insertedApp.getApp().getReleaseDate().getTime() > developer.getAdded().getTime()) {
                        // Safe notification
                        try {
                            InsertNotification insertNotification = new InsertNotification(connection,
                                    developer.getId(), insertedApp.getApp().getAppleId(), insertedApp.getSearchApp().getTitle());
                            logger.info("New released app detected, " +
                                            "releaseDate=\"{}\" > developerAdded=\"{}\", inserted notification={}",
                                    insertedApp.getApp().getReleaseDate(), developer.getAdded(),
                                    insertNotification.getNotification());
                        } catch (NotificationAlreadyAddedException e) {
                            logger.info("Notification already added, developer=\"{}\", app=\"{}\"",
                                    developer.getName(), insertedApp.getSearchApp().getTitle());
                        }
                    }
                }
                SelectApps selectApps = new SelectApps(connection, searchApps);
                List<SelectedApp> selectedApps = selectApps.getSelectedApps();
                if (selectedApps.size() > 0) {
                    InsertLinks insertLinks = new InsertLinks(connection, developer, check.getCountry(), selectedApps);
                }
                UpdateCheck updateCheck = new UpdateCheck(connection, check.getId());
                connection.commit();
            } catch (CheckForUpdateNotFoundException e) {
                logger.debug(e.getMessage());
                rollbackNoException(connection);
            } catch (InternalServerException e) {
                logger.warn(e.getMessage(), e);
                rollbackNoException(connection);
            }
        } catch (SQLException e) {
            logger.warn("Datasource unavailable, " + e.getMessage(), e);
        }
    }

    private void rollbackNoException(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.warn("Rollback failed, {}", e.getMessage(), e);
        }
    }
}
