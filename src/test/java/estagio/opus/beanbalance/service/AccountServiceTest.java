package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Account;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.enums.AccountType;
import estagio.opus.beanbalance.domain.repository.AccountRepository;
import estagio.opus.beanbalance.exception.ResourceNotFoundException;
import estagio.opus.beanbalance.web.dto.account.AccountRequest;
import estagio.opus.beanbalance.web.dto.account.AccountResponse;
import estagio.opus.beanbalance.web.mapper.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;
    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).build();
        account = Account.builder()
                .id(UUID.randomUUID())
                .name("Main Account")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .user(user)
                .build();
        accountResponse = new AccountResponse(
                account.getId(), "Main Account", AccountType.CHECKING,
                BigDecimal.valueOf(1000), LocalDateTime.now());
    }

    @Test
    void findAllByUser_shouldReturnUserAccounts() {
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(List.of(account));
        when(accountMapper.toResponse(account)).thenReturn(accountResponse);

        List<AccountResponse> result = accountService.findAllByUser(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Main Account");
    }

    @Test
    void create_shouldSaveAndReturnAccount() {
        var request = new AccountRequest("Savings", AccountType.SAVINGS, BigDecimal.valueOf(500));
        when(accountMapper.toEntity(request)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toResponse(account)).thenReturn(accountResponse);

        AccountResponse result = accountService.create(request, user);

        assertThat(result).isNotNull();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void findByIdAndUser_shouldThrowWhenNotFound() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findByIdAndUser(accountId, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveAccount() {
        when(accountRepository.findByIdAndUserId(account.getId(), user.getId()))
                .thenReturn(Optional.of(account));

        accountService.delete(account.getId(), user);

        verify(accountRepository).delete(account);
    }
}
