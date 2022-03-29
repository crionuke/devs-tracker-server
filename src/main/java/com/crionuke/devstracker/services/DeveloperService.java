package com.crionuke.devstracker.services;

import com.crionuke.devstracker.actions.*;
import com.crionuke.devstracker.actions.dto.Developer;
import com.crionuke.devstracker.actions.dto.DeveloperApp;
import com.crionuke.devstracker.services.dto.SearchDeveloper;
import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.api.apple.AppleApi;
import com.crionuke.devstracker.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component
public class DeveloperService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(DeveloperService.class);

    private final DataSource dataSource;
    private final AppleApi appleApi;

    DeveloperService(DataSource dataSource, AppleApi appleApi) {
        this.dataSource = dataSource;
        this.appleApi = appleApi;
    }

    public List<SearchDeveloper> search(List<String> countries, String term) throws InternalServerException {
        // TODO: Check arguments
        List<SearchDeveloper> developers = appleApi
                .searchDeveloper(countries, term)
                .flatMap(response -> Flux.fromIterable(response.getResults()))
                .map(result -> new SearchDeveloper(result.getArtistId(), result.getArtistName()))
                .distinct()
                .take(8)
                .collectList()
                .block();
        logger.debug("Got response from Apple SearchAPI, term=\"{}\", {}", term, developers);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                InsertDevelopers insertDevelopers = new InsertDevelopers(connection, developers);
                connection.commit();
            } catch (InternalServerException e) {
                rollbackNoException(connection);
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
        return developers;
    }

    public List<DeveloperApp> getApps(User user, long developerAppleId)
            throws DeveloperNotFoundException, TrackerForUpdateNotFoundException, InternalServerException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                SelectDeveloper selectDeveloper = new SelectDeveloper(connection, developerAppleId);
                Developer developer = selectDeveloper.getDeveloper();
                SelectDeveloperApps selectDeveloperApps = new SelectDeveloperApps(connection, developer.getId());
                UpdateTracker updateTracker = new UpdateTracker(connection, user.getId(), developer.getId());
                // Commit
                connection.commit();
                return selectDeveloperApps.getDeveloperApps();
            } catch (DeveloperNotFoundException | TrackerForUpdateNotFoundException | InternalServerException e) {
                rollbackNoException(connection);
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
    }
}
