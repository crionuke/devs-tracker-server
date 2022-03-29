package com.crionuke.devstracker.services;

import com.crionuke.devstracker.actions.SelectApp;
import com.crionuke.devstracker.actions.SelectAppLinks;
import com.crionuke.devstracker.actions.dto.App;
import com.crionuke.devstracker.actions.dto.AppLink;
import com.crionuke.devstracker.exceptions.AppNotFoundException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component
public class AppService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(AppService.class);

    private final DataSource dataSource;

    AppService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<AppLink> getLinks(long appAppleId)
            throws AppNotFoundException, InternalServerException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                SelectApp selectApp = new SelectApp(connection, appAppleId);
                App app = selectApp.getApp();
                SelectAppLinks selectAppLinks = new SelectAppLinks(connection, app.getId());
                // Commit
                connection.commit();
                return selectAppLinks.getLinks();
            } catch (AppNotFoundException | InternalServerException e) {
                rollbackNoException(connection);
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
    }
}
