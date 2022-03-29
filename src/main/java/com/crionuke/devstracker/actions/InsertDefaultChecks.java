package com.crionuke.devstracker.actions;

import com.crionuke.devstracker.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertDefaultChecks {
    private static final Logger logger = LoggerFactory.getLogger(InsertDefaultChecks.class);

    private final String[] COUNTRIES = {"us", "ru", "ch", "dk", "nl", "fi", "at", "au", "is", "de", "nz", "no", "ee", "om", "se", "si", "es", "jp", "pt", "lt", "ca", "gb", "cz", "hr", "ae", "qa", "be", "ie", "fr", "sk", "lv", "sa", "il", "cy", "sg", "tw", "it", "pl", "za", "ro", "kr", "gr", "bg", "tr", "uy", "cr", "mx", "ec", "my", "jo", "kw", "ar", "pa", "mk", "ua", "by", "pk", "br", "in", "cn", "az", "co", "th", "cl", "hk", "kz", "lb", "id", "vn", "eg", "pe", "lk", "ph", "ke", "ng"};

    private final String INSERT_SQL = "INSERT INTO checks (c_developer_id, c_country, c_priority) VALUES(?, ?, ?) ON CONFLICT DO NOTHING";

    public InsertDefaultChecks(Connection connection, long developerId) throws InternalServerException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, developerId);
            int priority = 0;
            for (String country : COUNTRIES) {
                statement.setString(2, country);
                statement.setInt(3, priority++);
                statement.addBatch();
            }
            int[] result = statement.executeBatch();
            logger.debug("Default checks inserted, developerId={}, countries={}, result={}",
                    developerId, COUNTRIES, result);
        } catch (SQLException e) {
            throw new InternalServerException("Transaction failed, " + e.getMessage(), e);
        }
    }
}
