package com.crionuke.devstracker.services;

import com.crionuke.devstracker.actions.*;
import com.crionuke.devstracker.actions.dto.Developer;
import com.crionuke.devstracker.actions.dto.TrackedDeveloper;
import com.crionuke.devstracker.actions.dto.Tracker;
import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.api.firebase.FirebaseApi;
import com.crionuke.devstracker.api.revenueCat.RevenueCatApi;
import com.crionuke.devstracker.api.revenueCat.dto.RevenueCatResponse;
import com.crionuke.devstracker.exceptions.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component
public class TrackerService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(TrackerService.class);

    private final int freeTrackersLimit;
    private final int maxTrackersLimit;
    private final DataSource dataSource;
    private final RevenueCatApi revenueCatApi;
    private final FirebaseApi firebaseApi;

    TrackerService(@Value("${devsTracker.freeTrackersLimit}") int freeTrackersLimit,
                   @Value("${devsTracker.maxTrackersLimit}") int maxTrackersLimit,
                   DataSource dataSource, RevenueCatApi revenueCatApi, FirebaseApi firebaseApi) {
        logger.info("Initialized, freeTrackersLimit={}, maxTrackersLimit={}", freeTrackersLimit, maxTrackersLimit);
        this.freeTrackersLimit = freeTrackersLimit;
        this.maxTrackersLimit = maxTrackersLimit;
        this.dataSource = dataSource;
        this.revenueCatApi = revenueCatApi;
        this.firebaseApi = firebaseApi;
    }

    public List<TrackedDeveloper> getDevelopers(User user) throws InternalServerException {
        try (Connection connection = dataSource.getConnection()) {
            try {
                SelectTrackedDevelopers selectTrackedDevelopers = new SelectTrackedDevelopers(connection, user.getId());
                return selectTrackedDevelopers.getTrackedDevelopers();
            } catch (InternalServerException e) {
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
    }

    public void trackDeveloper(User user, long developerAppleId) throws
            FreeTrackersLimitReachedException, MaxTrackersLimitReachedException, DeveloperNotFoundException,
            TrackerAlreadyAddedException, InternalServerException {
        // TODO: Check arguments
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String appUserId = user.getToken();
                RevenueCatResponse revenueCatResponse = revenueCatApi.getSubscriber(appUserId).block();
                logger.debug("Got subscriber, {}", revenueCatResponse);
                CountTrackers countTrackers = new CountTrackers(connection, user.getId());
                if (!revenueCatResponse.hasActiveEntitlement() &&
                        countTrackers.getCount() >= freeTrackersLimit) {
                    throw new FreeTrackersLimitReachedException(
                            "Free trackers limit reached, limit=" + freeTrackersLimit + ", user=" + user);
                }
                if (countTrackers.getCount() >= maxTrackersLimit) {
                    throw new MaxTrackersLimitReachedException(
                            "Max trackers limit reached, limit=" + maxTrackersLimit + ", user=" + user);
                }
                SelectDeveloper selectDeveloper = new SelectDeveloper(connection, developerAppleId);
                Developer developer = selectDeveloper.getDeveloper();
                InsertDefaultChecks insertDefaultChecks = new InsertDefaultChecks(connection, developer.getId());

                InsertTracker insertTracker = new InsertTracker(connection, user.getId(), developer.getId());
                try {
                    firebaseApi.subscribeDeviceToAppleDeveloper(user.getDevice(), developerAppleId);
                } catch (FirebaseMessagingException e) {
                    throw new InternalServerException("subscribe to topic failed", e);
                }
                // Commit
                connection.commit();
            } catch (FreeTrackersLimitReachedException | DeveloperNotFoundException |
                    TrackerAlreadyAddedException | InternalServerException e) {
                rollbackNoException(connection);
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
    }

    public void deleteTracker(User user, long developerAppleId) throws
            DeveloperNotFoundException, TrackerNotFoundException, InternalServerException {
        // TODO: Check arguments
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                SelectDeveloper selectDeveloper = new SelectDeveloper(connection, developerAppleId);
                Developer developer = selectDeveloper.getDeveloper();
                DeleteTracker deleteTracker = new DeleteTracker(connection, user.getId(), developer.getId());
                try {
                    firebaseApi.unsubscribeDeviceFromAppleDeveloper(user.getDevice(), developerAppleId);
                } catch (FirebaseMessagingException e) {
                    throw new InternalServerException("unsubscribe from topic failed", e);
                }
                // Commit
                connection.commit();
            } catch (DeveloperNotFoundException | InternalServerException e) {
                rollbackNoException(connection);
                throw e;
            }
        } catch (SQLException e) {
            throw new InternalServerException("Datasource unavailable, " + e.getMessage(), e);
        }
    }
}
