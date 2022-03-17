package com.example.flashcards.controller;

import com.example.flashcards.dto.UserDto;
import com.example.flashcards.dto.UserPasswordDto;
import com.example.flashcards.model.jwt.JwtRequest;
import com.example.flashcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    void create(@RequestBody UserDto userDto) {

        userService.createNewUser(userDto);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    Map<String, String> createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {

        return userService.createAuthenticationToken(request);
    }

    @PutMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    void updatePasswordForUser(final Authentication authentication, @RequestBody UserPasswordDto userPasswordDto) {

        userService.changePassword(authentication.getName(), userPasswordDto);
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    Map<String, String> getUserByUsername(final Authentication authentication) {
        return userService.getUserByUsername(authentication.getName());
    }
}
