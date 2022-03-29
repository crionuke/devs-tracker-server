package com.crionuke.devstracker.controllers;

import com.crionuke.devstracker.actions.dto.DeveloperApp;
import com.crionuke.devstracker.services.dto.SearchDeveloper;
import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.controllers.dto.DeveloperAppsResponse;
import com.crionuke.devstracker.controllers.dto.ErrorResponse;
import com.crionuke.devstracker.controllers.dto.SearchRequest;
import com.crionuke.devstracker.controllers.dto.SearchResponse;
import com.crionuke.devstracker.exceptions.DeveloperNotFoundException;
import com.crionuke.devstracker.exceptions.ForbiddenRequestException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.exceptions.TrackerForUpdateNotFoundException;
import com.crionuke.devstracker.services.DeveloperService;
import com.crionuke.devstracker.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/devstracker/v1/developers")
public class DeveloperController {
    private static final Logger logger = LoggerFactory.getLogger(DeveloperController.class);

    private final UserService userService;
    private final DeveloperService developerService;

    public DeveloperController(UserService userService, DeveloperService developerService) {
        this.userService = userService;
        this.developerService = developerService;
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResponse> search(@RequestHeader HttpHeaders headers,
                                                 @RequestBody SearchRequest request) {
        try {
            User user = userService.authenticate(headers);
            if (logger.isInfoEnabled()) {
                logger.info("Search developer, {}, user={}", request, user);
            }
            List<String> countries = request.getCountries();
            if (countries.size() > 3) {
                throw new ForbiddenRequestException("Too many countries per request, countries=" + countries);
            }
            List<SearchDeveloper> searchDevelopers = developerService.search(request.getCountries(), request.getTerm());
            return new ResponseEntity(new SearchResponse(searchDevelopers.size(), searchDevelopers), HttpStatus.OK);
        } catch (ForbiddenRequestException e) {
            logger.info(e.getMessage());
            return new ResponseEntity(new ErrorResponse(
                    ForbiddenRequestException.ID, e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (InternalServerException e) {
            logger.warn(e.getMessage(), e);
            return new ResponseEntity(new ErrorResponse(
                    InternalServerException.ID, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "{developerAppleId}/apps")
    public ResponseEntity getApps(@RequestHeader HttpHeaders headers, @PathVariable long developerAppleId) {
        try {
            User user = userService.authenticate(headers);
            if (logger.isInfoEnabled()) {
                logger.info("Get developer's apps, developerAppleId={}, user={}", developerAppleId, user);
            }
            List<DeveloperApp> developerApps = developerService.getApps(user, developerAppleId);
            return new ResponseEntity(
                    new DeveloperAppsResponse(developerApps), HttpStatus.OK);
        } catch (DeveloperNotFoundException e) {
            logger.info(e.getMessage());
            return new ResponseEntity(new ErrorResponse(
                    DeveloperNotFoundException.ID, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (TrackerForUpdateNotFoundException e) {
            logger.info(e.getMessage());
            return new ResponseEntity(new ErrorResponse(
                    TrackerForUpdateNotFoundException.ID, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (ForbiddenRequestException e) {
            logger.info(e.getMessage());
            return new ResponseEntity(new ErrorResponse(
                    ForbiddenRequestException.ID, e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (InternalServerException e) {
            logger.warn(e.getMessage(), e);
            return new ResponseEntity(new ErrorResponse(InternalServerException.ID, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
