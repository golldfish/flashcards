package com.example.flashcards.dto;

import com.example.flashcards.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {
    private String username;
    private String email;
    private String role;

    public static UserDetailsDto createFrom(final User user){
        return UserDetailsDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
