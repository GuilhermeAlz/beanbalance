package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.security.CurrentUserService;
import estagio.opus.beanbalance.service.TransactionService;
import estagio.opus.beanbalance.web.dto.transaction.TransactionRequest;
import estagio.opus.beanbalance.web.dto.transaction.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public Page<TransactionResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return transactionService.findAllByUser(currentUserService.getCurrentUser().localUserId(), pageable);
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable UUID id) {
        return transactionService.findByIdAndUser(id, currentUserService.getCurrentUser().localUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionRequest request) {
        return transactionService.create(request, currentUserService.getCurrentUser().localUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id, currentUserService.getCurrentUser().localUserId());
    }
}
