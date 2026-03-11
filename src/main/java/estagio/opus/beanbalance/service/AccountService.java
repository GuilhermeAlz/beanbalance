package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Account;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.repository.AccountRepository;
import estagio.opus.beanbalance.exception.ResourceNotFoundException;
import estagio.opus.beanbalance.web.dto.account.AccountRequest;
import estagio.opus.beanbalance.web.dto.account.AccountResponse;
import estagio.opus.beanbalance.web.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public List<AccountResponse> findAllByUser(User user) {
        return accountRepository.findAllByUserId(user.getId()).stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findByIdAndUser(UUID accountId, User user) {
        Account account = getAccountOrThrow(accountId, user.getId());
        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse create(AccountRequest request, User user) {
        Account account = accountMapper.toEntity(request);
        account.setUser(user);
        if (request.balance() != null) {
            account.setBalance(request.balance());
        }
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse update(UUID accountId, AccountRequest request, User user) {
        Account account = getAccountOrThrow(accountId, user.getId());
        accountMapper.updateEntity(request, account);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Transactional
    public void delete(UUID accountId, User user) {
        Account account = getAccountOrThrow(accountId, user.getId());
        accountRepository.delete(account);
    }

    private Account getAccountOrThrow(UUID accountId, UUID userId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }
}
