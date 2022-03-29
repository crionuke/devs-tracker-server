package com.crionuke.devstracker.services;

import com.crionuke.devstracker.actions.*;
import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.api.revenueCat.RevenueCatApi;
import com.crionuke.devstracker.api.revenueCat.dto.RevenueCatResponse;
import com.crionuke.devstracker.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class UserService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";
    private static final String DEVICE_HEADER_NAME = "Device";
    private static final String SIGN_HEADER_NAME = "Sign";

    private final boolean validateRequests;
    private final DataSource dataSource;

    UserService(@Value("${devsTracker.validateRequests}") boolean validateRequests, DataSource dataSource) {
        logger.info("Initialized, validateRequests={}", validateRequests);
        this.validateRequests = validateRequests;
        this.dataSource = dataSource;
    }

    public User signIn(HttpHeaders headers) throws ForbiddenRequestException, InternalServerException {
        String[] validation = handleHeaders(headers);
        String token = validation[0];
        String device = validation[1];
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                User user;
                try {
                    user = getUser(connection, token);
                } catch (UserNotFoundException e) {
                    user = createUser(connection, token, device);
                }
                connection.commit();
                return user;
            } catch (InternalServerException e) {
                rollbackNoException(connection);
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
    }

    public User authenticate(HttpHeaders headers) throws ForbiddenRequestException, InternalServerException {
        String[] validation = handleHeaders(headers);
        String token = validation[0];
        String device = validation[1];
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                User user = getUser(connection, token);
                // Update user device if changed
                if (!user.getDevice().equals(device)) {
                    UpdateUserDevice updateUserDevice = new UpdateUserDevice(connection, user, device);
                    user = updateUserDevice.getUpdatedUser();
                }
                connection.commit();
                return user;
            } catch (UserNotFoundException e) {
                rollbackNoException(connection);
                throw new ForbiddenRequestException("User not found, token=" +
                        token.substring(Math.max(0, token.length() - 8)));
            } catch (InternalServerException e) {
                rollbackNoException(connection);
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
    }

    private User getUser(Connection connection, String token) throws UserNotFoundException, InternalServerException {
        SelectUser selectUser = new SelectUser(connection, token);
        return selectUser.getUser();
//        try {
//            SelectAlias selectAlias = new SelectAlias(connection, token);
//            return selectAlias.getUser();
//        } catch (AliasNotFoundException e) {
//            SelectUser selectUser = new SelectUser(connection, token);
//            return selectUser.getUser();
//        }
    }

    private User createUser(Connection connection, String token, String device) throws InternalServerException {
        try {
            InsertUser insertUser = new InsertUser(connection, token, device);
            return insertUser.getUser();
        } catch (UserAlreadyAddedException e) {
            throw new InternalServerException("User not created, token=" +
                    "..." + token.substring(Math.max(0, token.length() - 8)), e);
        }
//        // Restore original user from revenue cat api
//        RevenueCatResponse revenueCatResponse = revenueCatApi.getSubscriber(token).block();
//        logger.debug("Got subscriber, {}", revenueCatResponse);
//        String originalUserToken = revenueCatResponse.getSubscriber().getOriginalAppUserId();
//        if (originalUserToken.equals(token)) {
//            try {
//                InsertUser insertUser = new InsertUser(connection, token, device);
//                return insertUser.getUser();
//            } catch (UserAlreadyAddedException e) {
//                throw new InternalServerException("User not created, token=" +
//                        "..." + token.substring(Math.max(0, token.length() - 8)), e);
//            }
//        } else {
//            try {
//                User originalUser;
//                try {
//                    SelectUser selectUser = new SelectUser(connection, originalUserToken);
//                    originalUser = selectUser.getUser();
//                } catch (UserNotFoundException e) {
//                    logger.warn("Original user not found and will be created, originalUserToken={}", originalUserToken);
//                    // If no original user to prevent error insert it
//                    InsertUser insertUser = new InsertUser(connection, originalUserToken, device);
//                    originalUser = insertUser.getUser();
//                }
//                InsertAlias insertAlias = new InsertAlias(connection, token, originalUser.getId());
//                return originalUser;
//            } catch (UserAlreadyAddedException | AliasAlreadyAddedException e) {
//                throw new InternalServerException("Alias not created, token=" +
//                        "..." + token.substring(Math.max(0, token.length() - 8)), e);
//            }
//        }
    }

    // Return array like [token, device];
    private String[] handleHeaders(HttpHeaders headers) throws ForbiddenRequestException {
        String header = headers.getFirst(AUTH_HEADER_NAME);
        if (header == null || !header.startsWith(AUTH_HEADER_PREFIX)) {
            throw new ForbiddenRequestException(AUTH_HEADER_NAME + " header not found");
        } else {
            String token = header.replace(AUTH_HEADER_PREFIX, "");
            String device = headers.getFirst(DEVICE_HEADER_NAME);
            if (device == null) {
                throw new ForbiddenRequestException(DEVICE_HEADER_NAME + " header not found");
            }
            String signString = headers.getFirst(SIGN_HEADER_NAME);
            if (signString == null) {
                throw new ForbiddenRequestException(SIGN_HEADER_NAME + " header not found");
            }
            long sign;
            try {
                sign = Long.parseLong(signString);
            } catch (NumberFormatException e) {
                throw new ForbiddenRequestException(SIGN_HEADER_NAME + " header wrong, value=" + signString);
            }
            boolean isValid = validateSign(token, device, sign);
            if (validateRequests && !isValid) {
                logger.warn("Wrong sign, sign={}, token=\"{}\", device=\"{}\"", sign, device);
                throw new ForbiddenRequestException(SIGN_HEADER_NAME + " header wrong, value=" + sign);
            } else {
                logger.debug("Request, sign={}, isValid={}, token=\"{}\", device=\"{}\"", sign, isValid,
                        "..." + token.substring(Math.max(0, token.length() - 8)),
                        "..." + device.substring(Math.max(0, device.length() - 8)));
            }
            return new String[]{token, device};
        }
    }

    private boolean validateSign(String token, String device, long sign) {
        long result = 0;
        int len1 = token.length();
        int len2 = device.length();
        for (char c : token.toCharArray()) {
            result += c + len1;
        }
        for (char c : device.toCharArray()) {
            result += c + len2;
        }
        return (result * result) == sign;
    }
}

