package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Account;
import estagio.opus.beanbalance.domain.repository.AccountRepository;
import estagio.opus.beanbalance.domain.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public List<AccountResponse> findAllByUser(UUID userId) {
        return accountRepository.findAllByUserId(userId).stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findByIdAndUser(UUID accountId, UUID userId) {
        Account account = getAccountOrThrow(accountId, userId);
        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse create(AccountRequest request, UUID userId) {
        Account account = accountMapper.toEntity(request);
        account.setUser(userRepository.getReferenceById(userId));
        if (request.balance() != null) {
            account.setBalance(request.balance());
        }
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse update(UUID accountId, AccountRequest request, UUID userId) {
        Account account = getAccountOrThrow(accountId, userId);
        accountMapper.updateEntity(request, account);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Transactional
    public void delete(UUID accountId, UUID userId) {
        Account account = getAccountOrThrow(accountId, userId);
        accountRepository.delete(account);
    }

    private Account getAccountOrThrow(UUID accountId, UUID userId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }
}
