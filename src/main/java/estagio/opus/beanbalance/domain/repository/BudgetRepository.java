package estagio.opus.beanbalance.domain.repository;

import estagio.opus.beanbalance.domain.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findAllByUserIdAndReferenceMonth(UUID userId, String referenceMonth);

    Optional<Budget> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndCategoryIdAndReferenceMonth(UUID userId, UUID categoryId, String referenceMonth);
}
