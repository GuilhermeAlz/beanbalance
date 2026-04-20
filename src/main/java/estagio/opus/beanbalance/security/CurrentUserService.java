package estagio.opus.beanbalance.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Convenience service that extracts the current JWT from the
 * {@link SecurityContextHolder} and delegates to {@link AuthenticatedUserResolver}
 * to return an {@link AuthenticatedUser}.
 * <p>
 * Usage in controllers / services:
 * <pre>
 *   AuthenticatedUser me = currentUserService.getCurrentUser();
 *   UUID userId = me.localUserId();
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final AuthenticatedUserResolver resolver;

    /**
     * Returns the {@link AuthenticatedUser} for the current request.
     *
     * @throws IllegalStateException if there is no authenticated JWT in the security context
     */
    public AuthenticatedUser getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return resolver.resolve(jwt);
        }

        throw new IllegalStateException(
                "No JWT authentication found in SecurityContext. "
                + "Is the request hitting a protected endpoint?");
    }
}
