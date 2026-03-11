package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.config.JwtService;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.repository.UserRepository;
import estagio.opus.beanbalance.exception.DuplicateResourceException;
import estagio.opus.beanbalance.web.dto.auth.AuthResponse;
import estagio.opus.beanbalance.web.dto.auth.LoginRequest;
import estagio.opus.beanbalance.web.dto.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return buildAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        String token = jwtService.generateToken(user);
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        var userSummary = new AuthResponse.UserSummary(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getRole().name());
        return new AuthResponse(token, userSummary);
    }
}
