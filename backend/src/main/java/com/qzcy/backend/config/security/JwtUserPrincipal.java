package com.qzcy.backend.config.security;

public record JwtUserPrincipal(Long userId, String username, String role) {
}
