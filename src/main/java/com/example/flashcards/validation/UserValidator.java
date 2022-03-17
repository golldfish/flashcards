package com.example.flashcards.validation;

import com.example.flashcards.dto.UserDto;
import com.example.flashcards.exception.BadRequestException;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9- +]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+([_ -]?[a-zA-Z0-9])*$";

    public void isRegisterUserValid(final UserDto userDto) {
        if (! isUsernameValid(userDto.getUsername())) {
            throw new BadRequestException("Invalid data");
        }

        if (! isEmailValid(userDto.getEmail())) {
            throw new BadRequestException("Invalid data");
        }

        if (! isPasswordValid(userDto.getPassword())) {
            throw new BadRequestException("Invalid data");
        }

        validatePasswords(userDto.getPassword(), userDto.getRepeatPassword());
    }

    public void comparePasswordToDb(final String inputtedPassword, final String dbPassword) {
        if (! BCrypt.checkpw(inputtedPassword, dbPassword)) {
            throw new BadRequestException("Invalid data");
        }
    }

    public void compareOldAndNewPassword(final String oldPasswd, final String newPasswd) {
        if (doPasswordsMatch(oldPasswd, newPasswd)) {
            throw new BadRequestException("Invalid data");
        }
    }

    public void validatePasswords(final String passwd1, final String passwd2) {
        if (! doPasswordsMatch(passwd1, passwd2)) {
            throw new BadRequestException("Invalid data");
        }
    }

    private boolean isUsernameValid(final String username) {
        return validateUsername(username);
    }

    private boolean validateUsername(final String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private boolean isEmailValid(final String email) {
        return validateEmail(email);
    }

    private boolean validateEmail(final String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(final String password) {
        PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(new LengthRule(8, 30),
//                new CharacterRule(EnglishCharacterData.UpperCase, 1),
//                new CharacterRule(EnglishCharacterData.Digit, 1),
//                new CharacterRule(EnglishCharacterData.Special, 1),
//                new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),
//                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),
//                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 3, false),
                new WhitespaceRule()));

        RuleResult result = passwordValidator.validate(new PasswordData(password));
        return result.isValid();
    }

    private boolean doPasswordsMatch(final String passwd1, final String passwd2) {
        return passwd1.equals(passwd2);
    }

}
