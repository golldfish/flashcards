package com.example.flashcards.validation;

import com.example.flashcards.dto.UserDto;
import com.example.flashcards.dto.UserPasswordDto;
import com.example.flashcards.exception.InvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.passay.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9- +]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
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
                        checkPasswordAndRepeatPassword(ud.getPassword(), ud.getRepeatPassword()),
                        checkPassword(ud.getPassword()),
                        checkPasswordWithDb(ud.getOldPassword(), dbPassword),
                        checkOldAndNewPasswordsEquals(ud.getOldPassword(), ud.getPassword()))
                .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private FieldError checkUsername(final String username) {
        if (StringUtils.isBlank(username) || username.length() > MAX_USERNAME_LENGTH || username.length() < MIN_USERNAME_LENGTH
                || !(Pattern.compile(USERNAME_PATTERN).matcher(username).matches())) {
            final String message = "Invalid username";
            return new FieldError("String", "username", username, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkEmail(final String email) {
        if (StringUtils.isBlank(email) || !(Pattern.compile(EMAIL_PATTERN).matcher(email).matches())) {
            final String message = "Invalid email";
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

        if (StringUtils.isBlank(password) || !(passwordValidator.validate(new PasswordData(password)).isValid())) {
            final String message = "Invalid password";
            return new FieldError("String", "password", password, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkPasswordAndRepeatPassword(final String password, final String repeatPassword) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(repeatPassword) || !(password.equals(repeatPassword))) {
            final String message = "Passwords must be equals";
            return new FieldError("String", "repeatPassword", repeatPassword, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkPasswordWithDb(final String password, final String dbPassword) {
        if (StringUtils.isBlank(password) || !BCrypt.checkpw(password, dbPassword)) {
            final String message = "Wrong password";
            return new FieldError("String", "password", password, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkOldAndNewPasswordsEquals(final String password, final String newPassword) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(newPassword) || (password.equals(newPassword))) {
            final String message = "Passwords cannot be equals";
            return new FieldError("String", "password", password, false, null, null, message);
        } else {
            return null;
        }
    }
}
