package estagio.opus.beanbalance.security;

import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.enums.Role;
import estagio.opus.beanbalance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves an external JWT into a local {@link AuthenticatedUser}.
 * <p>
 * If the user identified by the JWT's {@code sub} claim does not yet exist
 * locally, it is created on the fly (JIT provisioning) so that domain
 * entities (accounts, transactions, …) can reference a local {@code users} row.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedUserResolver {

    private final UserRepository userRepository;

    /**
     * Resolves (or creates) a local user from the given JWT and returns
     * a lightweight {@link AuthenticatedUser} projection.
     */
    @Transactional
    public AuthenticatedUser resolve(Jwt jwt) {
        String externalId = jwt.getSubject();                       
        String email      = jwt.getClaimAsString("email");
        String roleClaim  = jwt.getClaimAsString("role");
        String username   = jwt.getClaimAsString("username");

        User user = userRepository.findByExternalAuthId(externalId)
                .orElseGet(() -> provisionUser(externalId, email, username, roleClaim));

        return new AuthenticatedUser(
                externalId,
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );
    }

    private User provisionUser(String externalId, String email, String username, String roleClaim) {
        log.info("JIT provisioning local user for externalAuthId={} email={}", externalId, email);

        User user = User.builder()
                .externalAuthId(externalId)
                .email(email)
                .name(username != null ? username : email)
                .password("")                                   
                .role(mapRole(roleClaim))
                .build();

        return userRepository.save(user);
    }

    private Role mapRole(String roleClaim) {
        if (roleClaim == null || roleClaim.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(roleClaim.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown role claim '{}', defaulting to USER", roleClaim);
            return Role.USER;
        }
    }
}
