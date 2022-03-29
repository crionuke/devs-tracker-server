package com.crionuke.devstracker.controllers;

import com.crionuke.devstracker.actions.dto.AppLink;
import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.controllers.dto.AppLinksResponse;
import com.crionuke.devstracker.controllers.dto.ErrorResponse;
import com.crionuke.devstracker.exceptions.AppNotFoundException;
import com.crionuke.devstracker.exceptions.ForbiddenRequestException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.services.AppService;
import com.crionuke.devstracker.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/devstracker/v1/apps")
public class AppController {
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final UserService userService;
    private final AppService appService;

    public AppController(UserService userService, AppService appService) {
        this.userService = userService;
        this.appService = appService;
    }

    @GetMapping(value = "{appAppleId}/links")
    public ResponseEntity getLinks(@RequestHeader HttpHeaders headers, @PathVariable long appAppleId) {
        try {
            User user = userService.authenticate(headers);
            if (logger.isInfoEnabled()) {
                logger.info("Get apps's links, appAppleId={}, user={}", appAppleId, user);
            }
            List<AppLink> appLinks = appService.getLinks(appAppleId);
            return new ResponseEntity(new AppLinksResponse(appLinks), HttpStatus.OK);
        } catch (AppNotFoundException e) {
            logger.info(e.getMessage());
            return new ResponseEntity(new ErrorResponse(
                    AppNotFoundException.ID, e.getMessage()), HttpStatus.NOT_FOUND);
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
}
