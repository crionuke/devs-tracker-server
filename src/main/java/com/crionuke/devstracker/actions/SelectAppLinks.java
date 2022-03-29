package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.actions.dto.AppLink;
import com.crionuke.devstracker.actions.dto.Link;
import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectAppLinks {
    private static final Logger logger = LoggerFactory.getLogger(SelectAppLinks.class);

    private final String SELECT_SQL = "SELECT l_title, l_country, l_url FROM links WHERE l_app_id = ?";

    private final List<AppLink> links;

    public SelectAppLinks(Connection connection, long appId) throws InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setLong(1, appId);
            try (ResultSet resultSet = statement.executeQuery()) {
                links = new ArrayList<>();
                while (resultSet.next()) {
                    String linkTitle = resultSet.getString(1);
                    String linkCountry = resultSet.getString(2);
                    String linkUrl = resultSet.getString(3);
                    AppLink link = new AppLink(linkTitle, linkCountry, linkUrl);
                    links.add(link);
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }

    public List<AppLink> getLinks() {
        return links;
    }
}
