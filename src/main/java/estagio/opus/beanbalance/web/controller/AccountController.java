package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.security.CurrentUserService;
import estagio.opus.beanbalance.service.AccountService;
import estagio.opus.beanbalance.web.dto.account.AccountRequest;
import estagio.opus.beanbalance.web.dto.account.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAllByUser(currentUserService.getCurrentUser().localUserId());
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable UUID id) {
        return accountService.findByIdAndUser(id, currentUserService.getCurrentUser().localUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return accountService.create(request, currentUserService.getCurrentUser().localUserId());
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody AccountRequest request) {
        return accountService.update(id, request, currentUserService.getCurrentUser().localUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        accountService.delete(id, currentUserService.getCurrentUser().localUserId());
    }
}
