package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Account;
import estagio.opus.beanbalance.domain.entity.Category;
import estagio.opus.beanbalance.domain.entity.Transaction;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.enums.AccountType;
import estagio.opus.beanbalance.domain.enums.TransactionType;
import estagio.opus.beanbalance.domain.repository.AccountRepository;
import estagio.opus.beanbalance.domain.repository.CategoryRepository;
import estagio.opus.beanbalance.domain.repository.TransactionRepository;
import estagio.opus.beanbalance.domain.repository.UserRepository;
import estagio.opus.beanbalance.exception.ResourceNotFoundException;
import estagio.opus.beanbalance.web.dto.transaction.TransactionRequest;
import estagio.opus.beanbalance.web.dto.transaction.TransactionResponse;
import estagio.opus.beanbalance.web.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private UUID userId;
    private User user;
    private Account account;
    private Category category;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().id(userId).build();
        account = Account.builder()
                .id(UUID.randomUUID())
                .name("Main")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .user(user)
                .build();
        category = Category.builder()
                .id(UUID.randomUUID())
                .name("Food")
                .build();
        transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.EXPENSE)
                .date(LocalDate.now())
                .account(account)
                .category(category)
                .user(user)
                .build();
    }

    @Test
    void findAllByUser_shouldReturnPagedTransactions() {
        var pageable = PageRequest.of(0, 20);
        var response = new TransactionResponse(
                transaction.getId(), BigDecimal.valueOf(50), TransactionType.EXPENSE,
                null, LocalDate.now(), account.getId(), "Main",
                category.getId(), "Food", LocalDateTime.now());

        when(transactionRepository.findAllByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.toResponse(transaction)).thenReturn(response);

        Page<TransactionResponse> result = transactionService.findAllByUser(userId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void create_shouldSaveTransactionAndUpdateBalance() {
        var request = new TransactionRequest(
                BigDecimal.valueOf(50), TransactionType.EXPENSE,
                "Lunch", LocalDate.now(), account.getId(), category.getId());
        var response = new TransactionResponse(
                UUID.randomUUID(), BigDecimal.valueOf(50), TransactionType.EXPENSE,
                "Lunch", LocalDate.now(), account.getId(), "Main",
                category.getId(), "Food", LocalDateTime.now());

        when(accountRepository.findByIdAndUserId(account.getId(), userId))
                .thenReturn(Optional.of(account));
        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(response);

        TransactionResponse result = transactionService.create(request, userId);

        assertThat(result).isNotNull();
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(950));
        verify(accountRepository).save(account);
    }

    @Test
    void create_shouldThrowWhenAccountNotFound() {
        var request = new TransactionRequest(
                BigDecimal.valueOf(50), TransactionType.EXPENSE,
                "Lunch", LocalDate.now(), UUID.randomUUID(), category.getId());

        when(accountRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(request, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldReverseBalanceAndRemoveTransaction() {
        when(transactionRepository.findByIdAndUserId(transaction.getId(), userId))
                .thenReturn(Optional.of(transaction));

        transactionService.delete(transaction.getId(), userId);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1050));
        verify(transactionRepository).delete(transaction);
    }
}
