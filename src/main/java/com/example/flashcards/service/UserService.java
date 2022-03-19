package com.example.flashcards.service;

import com.example.flashcards.configuration.JwtTokenUtil;
import com.example.flashcards.dto.UserDetailsDto;
import com.example.flashcards.dto.UserDto;
import com.example.flashcards.dto.UserLoginDto;
import com.example.flashcards.dto.UserPasswordDto;
import com.example.flashcards.exception.ConflictException;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.User;
import com.example.flashcards.model.jwt.JwtRequest;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.validation.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final JwtUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;

    @Transactional
    public void createNewUser(final UserDto userDto) {
        userValidator.isRegisterUserValid(userDto);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.findUserByUsernameOrEmail(userDto.getUsername(), userDto.getEmail()).ifPresentOrElse(user -> {
            throw new ConflictException("User already exist");
        }, () -> {
            final User user = User.builder().username(userDto.getUsername()).email(userDto.getEmail()).password(userDto.getPassword()).build();
            userRepository.save(user);
        });
    }

    @Transactional
    public void changePassword(final String username, final UserPasswordDto userPasswordDto) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        userValidator.comparePasswordToDb(userPasswordDto.getOldPassword(), user.getPassword());
        userValidator.compareOldAndNewPassword(userPasswordDto.getOldPassword(), userPasswordDto.getPassword());
        userValidator.validatePasswords(userPasswordDto.getPassword(), userPasswordDto.getRepeatPassword());
        userPasswordDto.setPassword(passwordEncoder.encode(userPasswordDto.getPassword()));
        user.setPassword(userPasswordDto.getPassword());
        userRepository.save(user);
    }

    public UserLoginDto loginAndCreateToken(final JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final User user = userRepository.findUserByUsername(authenticationRequest.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        return UserLoginDto.createFrom(user, jwtTokenUtil.generateToken(userDetails));
    }

    @Transactional(readOnly = true)
    public UserDetailsDto getUserByUsername(final String username) throws NotFoundException {
        return UserDetailsDto.createFrom(userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found")));
    }

    private void authenticate(final String username, final String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
