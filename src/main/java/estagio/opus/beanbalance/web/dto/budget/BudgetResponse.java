package estagio.opus.beanbalance.web.dto.budget;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        String referenceMonth,
        UUID categoryId,
        String categoryName,
        LocalDateTime createdAt
) {}
