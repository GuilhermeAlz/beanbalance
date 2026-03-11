package estagio.opus.beanbalance.web.dto.category;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        Boolean custom,
        LocalDateTime createdAt
) {}
