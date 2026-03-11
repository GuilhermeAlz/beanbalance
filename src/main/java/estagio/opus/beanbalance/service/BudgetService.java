package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Budget;
import estagio.opus.beanbalance.domain.entity.Category;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.enums.TransactionType;
import estagio.opus.beanbalance.domain.repository.BudgetRepository;
import estagio.opus.beanbalance.domain.repository.CategoryRepository;
import estagio.opus.beanbalance.domain.repository.TransactionRepository;
import estagio.opus.beanbalance.exception.DuplicateResourceException;
import estagio.opus.beanbalance.exception.ResourceNotFoundException;
import estagio.opus.beanbalance.web.dto.budget.BudgetRequest;
import estagio.opus.beanbalance.web.dto.budget.BudgetResponse;
import estagio.opus.beanbalance.web.mapper.BudgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetMapper budgetMapper;

    @Transactional(readOnly = true)
    public List<BudgetResponse> findAllByUserAndMonth(User user, String referenceMonth) {
        return budgetRepository.findAllByUserIdAndReferenceMonth(user.getId(), referenceMonth).stream()
                .map(budget -> enrichWithSpentAmount(budget, user.getId()))
                .toList();
    }

    @Transactional
    public BudgetResponse create(BudgetRequest request, User user) {
        String referenceMonth = request.referenceMonth();

        if (budgetRepository.existsByUserIdAndCategoryIdAndReferenceMonth(
                user.getId(), request.categoryId(), referenceMonth)) {
            throw new DuplicateResourceException("Budget already exists for this category and month");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Budget budget = Budget.builder()
                .limitAmount(request.limitAmount())
                .referenceMonth(referenceMonth)
                .category(category)
                .user(user)
                .build();

        Budget saved = budgetRepository.save(budget);
        return enrichWithSpentAmount(saved, user.getId());
    }

    @Transactional
    public BudgetResponse update(UUID budgetId, BudgetRequest request, User user) {
        Budget budget = getBudgetOrThrow(budgetId, user.getId());
        budget.setLimitAmount(request.limitAmount());
        Budget saved = budgetRepository.save(budget);
        return enrichWithSpentAmount(saved, user.getId());
    }

    @Transactional
    public void delete(UUID budgetId, User user) {
        Budget budget = getBudgetOrThrow(budgetId, user.getId());
        budgetRepository.delete(budget);
    }

    private BudgetResponse enrichWithSpentAmount(Budget budget, UUID userId) {
        YearMonth month = YearMonth.parse(budget.getReferenceMonth());
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        BigDecimal spent = transactionRepository.sumByUserAndCategoryAndTypeInPeriod(
                userId, budget.getCategory().getId(), TransactionType.EXPENSE, startDate, endDate);

        return budgetMapper.toResponseWithSpent(budget, spent);
    }

    private Budget getBudgetOrThrow(UUID budgetId, UUID userId) {
        return budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));
    }
}
