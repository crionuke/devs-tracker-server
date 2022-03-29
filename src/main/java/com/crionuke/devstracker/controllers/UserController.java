package com.crionuke.devstracker.controllers;

import com.crionuke.devstracker.actions.dto.User;
import com.crionuke.devstracker.controllers.dto.ErrorResponse;
import com.crionuke.devstracker.exceptions.ForbiddenRequestException;
import com.crionuke.devstracker.exceptions.InternalServerException;
import com.crionuke.devstracker.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/devstracker/v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signIn")
    public ResponseEntity signIn(@RequestHeader HttpHeaders headers) {
        try {
            User user = userService.signIn(headers);
            if (logger.isInfoEnabled()) {
                logger.info("User sign-in, user={}", user);
            }
            return new ResponseEntity(HttpStatus.OK);
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
