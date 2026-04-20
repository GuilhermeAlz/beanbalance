package estagio.opus.beanbalance.security;

import java.util.UUID;

/**
 * Represents the currently authenticated user resolved from an external JWT.
 * Decoupled from the JPA User entity — controllers and services should use this
 * instead of {@code @AuthenticationPrincipal User}.
 */
public record AuthenticatedUser(
        String externalAuthId,
        String email,
        String role,
        UUID localUserId
) {}
