package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.service.TransactionService;
import estagio.opus.beanbalance.web.dto.transaction.TransactionRequest;
import estagio.opus.beanbalance.web.dto.transaction.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Page<TransactionResponse> findAll(@AuthenticationPrincipal User user,
                                             @PageableDefault(size = 20) Pageable pageable) {
        return transactionService.findAllByUser(user, pageable);
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return transactionService.findByIdAndUser(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionRequest request,
                                      @AuthenticationPrincipal User user) {
        return transactionService.create(request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        transactionService.delete(id, user);
    }
}
