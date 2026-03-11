package estagio.opus.beanbalance.web.dto.transaction;

import estagio.opus.beanbalance.domain.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type,
        @Size(max = 255) String description,
        @NotNull LocalDate date,
        @NotNull UUID accountId,
        @NotNull UUID categoryId
) {}
