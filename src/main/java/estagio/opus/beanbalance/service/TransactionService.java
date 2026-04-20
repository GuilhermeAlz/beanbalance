package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Account;
import estagio.opus.beanbalance.domain.entity.Category;
import estagio.opus.beanbalance.domain.entity.Transaction;
import estagio.opus.beanbalance.domain.enums.TransactionType;
import estagio.opus.beanbalance.domain.repository.AccountRepository;
import estagio.opus.beanbalance.domain.repository.CategoryRepository;
import estagio.opus.beanbalance.domain.repository.TransactionRepository;
import estagio.opus.beanbalance.domain.repository.UserRepository;
import estagio.opus.beanbalance.exception.ResourceNotFoundException;
import estagio.opus.beanbalance.web.dto.transaction.TransactionRequest;
import estagio.opus.beanbalance.web.dto.transaction.TransactionResponse;
import estagio.opus.beanbalance.web.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findAllByUser(UUID userId, Pageable pageable) {
        return transactionRepository.findAllByUserId(userId, pageable)
                .map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse findByIdAndUser(UUID transactionId, UUID userId) {
        Transaction transaction = getTransactionOrThrow(transactionId, userId);
        return transactionMapper.toResponse(transaction);
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(request.accountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.accountId()));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Transaction transaction = Transaction.builder()
                .amount(request.amount())
                .type(request.type())
                .description(request.description())
                .date(request.date())
                .account(account)
                .category(category)
                .user(userRepository.getReferenceById(userId))
                .build();

        updateAccountBalance(account, request.amount(), request.type());
        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID transactionId, UUID userId) {
        Transaction transaction = getTransactionOrThrow(transactionId, userId);
        reverseAccountBalance(transaction);
        transactionRepository.delete(transaction);
    }

    private void updateAccountBalance(Account account, BigDecimal amount, TransactionType type) {
        BigDecimal newBalance = type == TransactionType.INCOME
                ? account.getBalance().add(amount)
                : account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    private void reverseAccountBalance(Transaction transaction) {
        Account account = transaction.getAccount();
        BigDecimal reversed = transaction.getType() == TransactionType.INCOME
                ? account.getBalance().subtract(transaction.getAmount())
                : account.getBalance().add(transaction.getAmount());
        account.setBalance(reversed);
        accountRepository.save(account);
    }

    private Transaction getTransactionOrThrow(UUID transactionId, UUID userId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));
    }
}
