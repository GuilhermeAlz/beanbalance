package estagio.opus.beanbalance.domain.repository;

import estagio.opus.beanbalance.domain.entity.Transaction;
import estagio.opus.beanbalance.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
            WHERE t.user.id = :userId
              AND t.category.id = :categoryId
              AND t.type = :type
              AND t.date BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumByUserAndCategoryAndTypeInPeriod(
            UUID userId, UUID categoryId, TransactionType type,
            LocalDate startDate, LocalDate endDate);
}
