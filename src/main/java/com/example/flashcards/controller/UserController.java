package com.example.flashcards.controller;

import com.example.flashcards.dto.UserDetailsDto;
import com.example.flashcards.dto.UserDto;
import com.example.flashcards.dto.UserLoginDto;
import com.example.flashcards.dto.UserPasswordDto;
import com.example.flashcards.model.jwt.JwtRequest;
import com.example.flashcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    @Operation(summary = "Create user.",
            responses = {@ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "409", description = "User already exist", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Email, displayName or password not valid")})
    void create(@RequestBody UserDto userDto) {

        userService.createNewUser(userDto);
    }

    @PostMapping(value = "/login")
    @ResponseStatus(OK)
    @Operation(summary = "Login using email and password.",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully logged in"),
                    @ApiResponse(responseCode = "400", description = "Email or password is not valid"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    UserLoginDto createAuthenticationToken(@RequestBody JwtRequest request) {

        return userService.loginAndCreateToken(request);
    }

    @PutMapping(value = "/user")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Update user password",
            responses = {@ApiResponse(responseCode = "200", description = "Password updated"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    void updatePasswordForUser(final Authentication authentication, @RequestBody UserPasswordDto userPasswordDto) {

        userService.changePassword(authentication.getName(), userPasswordDto);
    }

    @GetMapping(value = "/user")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get user data",
            responses = {@ApiResponse(responseCode = "200", description = "Data collected"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    UserDetailsDto getUserByUsername(final Authentication authentication) {

        return userService.getUserByUsername(authentication.getName());
    }

    @DeleteMapping(value = "/user/delete")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Delete user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted")
            })
    void deleteUser(final Authentication authentication) {
        userService.removeUser(authentication.getName());
    }
}
