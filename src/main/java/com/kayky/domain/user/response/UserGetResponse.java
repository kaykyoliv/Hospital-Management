package com.kayky.domain.user.response;

public record UserGetResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {}
