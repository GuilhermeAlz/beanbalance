package estagio.opus.beanbalance.web.dto.auth;

public record AuthResponse(String token, UserSummary user) {

    public record UserSummary(String id, String name, String email, String role) {}
}
