package estagio.opus.beanbalance.web.dto.transaction;

import estagio.opus.beanbalance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        TransactionType type,
        String description,
        LocalDate date,
        UUID accountId,
        String accountName,
        UUID categoryId,
        String categoryName,
        LocalDateTime createdAt
) {}
