package com.crionuke.devstracker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    void rollbackNoException(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.warn("Rollback failed, {}", e.getMessage(), e);
        }
    }
}
