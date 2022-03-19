package com.example.flashcards.dto;

import com.example.flashcards.model.User;
import lombok.*;

@Data
@Value
@Builder
@RequiredArgsConstructor
public class UserLoginDto {
    String username;
    String email;
    String token;

    public static UserLoginDto createFrom(final User user, final String token) {
        return UserLoginDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }
}
