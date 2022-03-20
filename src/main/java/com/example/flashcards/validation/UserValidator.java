package com.example.flashcards.validation;

import com.example.flashcards.dto.UserDto;
import com.example.flashcards.dto.UserPasswordDto;
import com.example.flashcards.exception.InvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9- +]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+([_ -]?[a-zA-Z0-9])*$";


    public void validateUserCreateParameters(final UserDto userDto) {
        final List<FieldError> fieldErrorList = getFieldErrors(userDto);

        if (!fieldErrorList.isEmpty()) {
            throw new InvalidArgumentException(fieldErrorList);
        }
    }

    public void validateChangePasswordParameters(final UserPasswordDto userPasswordDto, final String dbPassword) {
        final List<FieldError> fieldErrorList = getFieldErrors(userPasswordDto, dbPassword);

        if (!fieldErrorList.isEmpty()) {
            throw new InvalidArgumentException(fieldErrorList);
        }
    }

    private List<FieldError> getFieldErrors(final UserDto userDto) {

        return Optional.ofNullable(userDto).map(ud -> Stream.of(checkUsername(ud.getUsername()),
                        checkEmail(ud.getEmail()),
                        checkPassword(ud.getPassword()),
                        checkPasswordAndRepeatPassword(ud.getPassword(), ud.getRepeatPassword()))
                .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private List<FieldError> getFieldErrors(final UserPasswordDto userPasswordDto, final String dbPassword) {

        return Optional.ofNullable(userPasswordDto).map(ud -> Stream.of(
                        checkPasswordWithDb(ud.getOldPassword(), dbPassword),
                        checkOldAndNewPasswordsEquals(ud.getOldPassword(), ud.getPassword()),
                        checkPasswordAndRepeatPassword(ud.getPassword(), ud.getRepeatPassword()),
                        checkPassword(ud.getPassword()))
                .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private FieldError checkUsername(final String username) {
        if (nonNull(username) && (username.isBlank() || (username.length() > MAX_USERNAME_LENGTH || username.length() < MIN_USERNAME_LENGTH))
                && !(Pattern.compile(USERNAME_PATTERN).matcher(username).matches())) {
            final String message = "Name cannot be blank or null and must be in range 3-32 characters";
            return new FieldError("String", "username", username, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkEmail(final String email) {
        if (nonNull(email) && email.isBlank() && !(Pattern.compile(EMAIL_PATTERN).matcher(email).matches())) {
            final String message = "Name cannot be blank or null and must be in range 3-32 characters";
            return new FieldError("String", "email", email, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkPassword(final String password) {
        final PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(new LengthRule(8, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 3, false),
                new WhitespaceRule()));

        if (nonNull(password) && password.isBlank() && !(passwordValidator.validate(new PasswordData(password)).isValid())) {
            final String message = "Password cannot be: blank or null and contains: 8-30 characters, 1 Upper case, 1 digit, 1 special character";
            return new FieldError("String", "password", password, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkPasswordAndRepeatPassword(final String password, final String repeatPassword) {

        if (nonNull(password) && password.isBlank() && nonNull(repeatPassword) && repeatPassword.isBlank()
                && !(password.equals(repeatPassword))) {
            final String message = "Passwords must be equals";
            return new FieldError("String", "repeatPassword", repeatPassword, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkPasswordWithDb(final String password, final String dbPassword) {
        if (!BCrypt.checkpw(password, dbPassword)) {
            final String message = "Wrong password";
            return new FieldError("String", "password", password, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkOldAndNewPasswordsEquals(final String password, final String newPassword) {
        if (nonNull(password) && password.isBlank() && nonNull(newPassword) && newPassword.isBlank()
                && (password.equals(newPassword))) {
            final String message = "Passwords cannot be equals";
            return new FieldError("String", "password", password, false, null, null, message);
        } else {
            return null;
        }
    }
}
