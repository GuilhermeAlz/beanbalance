package estagio.opus.beanbalance.web.dto.account;

import estagio.opus.beanbalance.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String name,
        AccountType type,
        BigDecimal balance,
        LocalDateTime createdAt
) {}
