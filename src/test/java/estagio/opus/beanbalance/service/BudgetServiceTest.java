package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Budget;
import estagio.opus.beanbalance.domain.entity.Category;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.enums.TransactionType;
import estagio.opus.beanbalance.domain.repository.BudgetRepository;
import estagio.opus.beanbalance.domain.repository.CategoryRepository;
import estagio.opus.beanbalance.domain.repository.TransactionRepository;
import estagio.opus.beanbalance.domain.repository.UserRepository;
import estagio.opus.beanbalance.exception.DuplicateResourceException;
import estagio.opus.beanbalance.web.dto.budget.BudgetRequest;
import estagio.opus.beanbalance.web.dto.budget.BudgetResponse;
import estagio.opus.beanbalance.web.mapper.BudgetMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetService budgetService;

    private UUID userId;
    private User user;
    private Category category;
    private Budget budget;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().id(userId).build();
        category = Category.builder().id(UUID.randomUUID()).name("Food").build();
        budget = Budget.builder()
                .id(UUID.randomUUID())
                .limitAmount(BigDecimal.valueOf(500))
                .referenceMonth("2026-03")
                .category(category)
                .user(user)
                .build();
    }

    @Test
    void findAllByUserAndMonth_shouldReturnBudgetsWithSpentAmount() {
        String month = "2026-03";
        var response = new BudgetResponse(
                budget.getId(), BigDecimal.valueOf(500), BigDecimal.valueOf(200),
                BigDecimal.valueOf(300), "2026-03", category.getId(), "Food", LocalDateTime.now());

        when(budgetRepository.findAllByUserIdAndReferenceMonth(userId, month))
                .thenReturn(List.of(budget));
        when(transactionRepository.sumByUserAndCategoryAndTypeInPeriod(
                eq(userId), eq(category.getId()), eq(TransactionType.EXPENSE),
                any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(200));
        when(budgetMapper.toResponseWithSpent(budget, BigDecimal.valueOf(200))).thenReturn(response);

        List<BudgetResponse> result = budgetService.findAllByUserAndMonth(userId, month);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().spentAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(result.getFirst().remainingAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void create_shouldThrowWhenDuplicateBudget() {
        var request = new BudgetRequest(BigDecimal.valueOf(500), "2026-03", category.getId());
        when(budgetRepository.existsByUserIdAndCategoryIdAndReferenceMonth(
                userId, category.getId(), "2026-03"))
                .thenReturn(true);

        assertThatThrownBy(() -> budgetService.create(request, userId))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void create_shouldSaveBudget() {
        var request = new BudgetRequest(BigDecimal.valueOf(500), "2026-03", category.getId());
        var response = new BudgetResponse(
                budget.getId(), BigDecimal.valueOf(500), BigDecimal.ZERO,
                BigDecimal.valueOf(500), "2026-03", category.getId(), "Food", LocalDateTime.now());

        when(budgetRepository.existsByUserIdAndCategoryIdAndReferenceMonth(any(), any(), any()))
                .thenReturn(false);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        when(transactionRepository.sumByUserAndCategoryAndTypeInPeriod(any(), any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(budgetMapper.toResponseWithSpent(any(), any())).thenReturn(response);

        BudgetResponse result = budgetService.create(request, userId);

        assertThat(result.limitAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        verify(budgetRepository).save(any(Budget.class));
    }
}
