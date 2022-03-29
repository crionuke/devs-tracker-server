package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.Developer;
import com.crionuke.devstracker.actions.dto.Link;
import com.crionuke.devstracker.actions.dto.SelectedApp;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertLinks {
    private static final Logger logger = LoggerFactory.getLogger(InsertLinks.class);

    private final String INSERT_SQL = "INSERT INTO links (l_app_id, l_title, l_country, l_url) VALUES(?, ?, ?, ?) ON CONFLICT DO NOTHING";

    private List<Link> insertedLinks;

    public InsertLinks(Connection connection, Developer developer, String country, List<SelectedApp> selectedApps)
            throws InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (SelectedApp selectedApp : selectedApps) {
                statement.setLong(1, selectedApp.getApp().getId());
                statement.setString(2, selectedApp.getSearchApp().getTitle());
                statement.setString(3, country);
                statement.setString(4, selectedApp.getSearchApp().getUrl());
                statement.addBatch();
            }
            statement.executeBatch();
            insertedLinks = new ArrayList<>();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                while (generatedKeys.next()) {
                    long linkId = generatedKeys.getLong("l_id");
                    Timestamp linkAdded = generatedKeys.getTimestamp("l_added");
                    long linkAppId = generatedKeys.getLong("l_app_id");
                    String linkTitle = generatedKeys.getString("l_title");
                    String linkCountry = generatedKeys.getString("l_country");
                    String linkUrl = generatedKeys.getString("l_url");
                    Link link = new Link(linkId, linkAdded, linkAppId, linkTitle, linkCountry, linkUrl);
                    insertedLinks.add(link);
                }
            }
            if (insertedLinks.size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Links inserted, developer=\"{}\", country=\"{}\", insertedCount={}",
                            developer.getName(), country, insertedLinks.size());
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Links inserted, developer=\"{}\", country=\"{}\", insertedCount={}, insertedLinks={}",
                            developer.getName(), country, insertedLinks.size(), insertedLinks);
                }
            } else {
                logger.debug("No new links inserted for apps, country={}", country);
            }

        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }
}
