package estagio.opus.beanbalance.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String message,
        Map<String, String> fieldErrors,
        LocalDateTime timestamp
) {
    public ApiError(int status, String message) {
        this(status, message, Map.of(), LocalDateTime.now());
    }

    public ApiError(int status, String message, Map<String, String> fieldErrors) {
        this(status, message, fieldErrors, LocalDateTime.now());
    }
}
